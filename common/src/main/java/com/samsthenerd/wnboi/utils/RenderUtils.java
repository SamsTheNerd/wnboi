package com.samsthenerd.wnboi.utils;

import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.wnboi.WNBOI;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;

/*
 * just some functions for rendering math.
 * mostly point calculations 
 */
public class RenderUtils {
    
    // returns points that make up the outline of an arc.
    // returns numDivisons+1 points
    // angle starts at 0 and goes counter clockwise to 2pi
    public static List<Vec3d> calcArcPoints(int numDivisions, double radius, double startAngle, double endAngle){
        List<Vec3d> points = new ArrayList<Vec3d>();
        double angle = startAngle;
        double angleStep = (endAngle - startAngle) / numDivisions;
        if(radius < 0){
            radius = 0; // make sure it's not negative
        }
        for(int i = 0; i <= numDivisions; i++){
            points.add(new Vec3d(radius * Math.cos(angle), radius * Math.sin(angle), 0));
            angle += angleStep;
        }
        return points;
    }

    public static Vec3d calcNormal(Vec3d point){
        return new Vec3d(-point.y, point.x, 0);
    }

    public static List<Vec3d> calcStroke(List<Vec3d> points, double outerStrokeWidth, double innerStrokeWidth){
        List<Vec3d> stroke = new ArrayList<Vec3d>();
        if(points.size() <= 1){
            return points; // figure this out when we figure out caps
        }
        // store values as we go so that we don't have to recalculate as much
        Vec3d del1 = points.get(1).subtract(points.get(0));
        Vec3d del2 = del1;
        // vars are parts that get multiplied by stroke width
        double AValVar;
        double AValConst;
        double BValVar = Math.sqrt(del2.x*del2.x + del2.y*del2.y);
        double BValConst = del1.y * points.get(0).x - del1.x * points.get(1).y;
        Vec3d p1;
        Vec3d p2 = points.get(0);
        Vec3d p3 = p2;
        boolean isClosed = false;
        for(int i = 0; i < points.size(); i++){
            del1 = del2;
            AValVar = BValVar;
            AValConst = BValConst;
            p1 = p2;
            p2 = p3;
            if(i == points.size() - 1){ // last point 
                if(points.get(0).equals(p3)){ //closed path, get first point
                    p3 = points.get(1);
                    isClosed = true;
                }
            } else { // not last point
                p3 = points.get(i+1);
            }
            del2 = p3.subtract(p2);
            BValVar = Math.sqrt(del2.x*del2.x + del2.y*del2.y);
            BValConst = del2.y * p2.x - del2.x * p2.y;

            double det = del1.x * del2.y - del1.y * del2.x;
            if(det == 0){ // straight line, add halfway points to avoid messing up color somehow
                Vec3d norm;
                if(i == 0){
                    norm = (new Vec3d(del2.y, -del2.x,0)).normalize();
                } else {
                    norm = (new Vec3d(del1.y, -del1.x,0)).normalize();
                }
                stroke.add(p2.add(norm.multiply(outerStrokeWidth)));
                stroke.add(p2.add(norm.multiply(-innerStrokeWidth)));
            } else {
                // double AValOuter = AValConst + outerStrokeWidth * AValVar;
                // double AValInner = AValConst - innerStrokeWidth * AValVar;
                // double BValOuter = BValConst + outerStrokeWidth * BValVar;
                // double BValInner = BValConst - innerStrokeWidth * BValVar;
                double AValOuter = AValConst + innerStrokeWidth * AValVar;
                double AValInner = AValConst - outerStrokeWidth * AValVar;
                double BValOuter = BValConst + innerStrokeWidth * BValVar;
                double BValInner = BValConst - outerStrokeWidth * BValVar;
                // get raw values, then we'll scale with stroke width to get the actual points
                double outerX = (BValOuter * del1.x - AValOuter * del2.x) / det;
                double outerY = (BValOuter * del1.y - AValOuter * del2.y) / det;
                stroke.add(new Vec3d(outerX, outerY, 0));
                
                double innerX = (BValInner * del1.x - AValInner * del2.x) / det;
                double innerY = (BValInner * del1.y - AValInner * del2.y) / det;
                stroke.add(new Vec3d(innerX, innerY, 0));

                if(isClosed){ // add first point again
                    stroke.set(0, (new Vec3d(outerX, outerY, 0)));
                    stroke.set(1, new Vec3d(innerX, innerY, 0));
                }

                // maybe need to add a thing to override first point if it's closed
            }
        }
        return stroke;
    }

    
    public static List<Vec3d> drawStroke(BufferBuilder buffer, List<Vec3d> points, double outerStrokeWidth, double innerStrokeWidth){
        List<Vec3d> stroke = calcStroke(points, outerStrokeWidth, innerStrokeWidth);
        // WNBOI.LOGGER.info("stroke size: " + stroke.size() + " from points size: " + points.size());
        // draw stroke
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP , VertexFormats.POSITION_COLOR);
        for(int i = 0; i < stroke.size(); i++){
            // WNBOI.LOGGER.info("stroke point: " + stroke.get(i).x + ", " + stroke.get(i).y);
            buffer.vertex(stroke.get(i).x, stroke.get(i).y, 0).color(128,128,128,255).next();
        }
        return stroke; // pass it up just in case
    }

    public static void testCalcStroke(){
        List<Vec3d> points1 = new ArrayList<Vec3d>();
        points1.add(new Vec3d(0,0,0));
        points1.add(new Vec3d(0.5,1,0));
        points1.add(new Vec3d(1,1,0));
        points1.add(new Vec3d(1,0,0));
        points1.add(new Vec3d(0,0,0));
        List<Vec3d> stroke1 = calcStroke(points1, 0.1, 0.1);
        WNBOI.LOGGER.info("test 1:");
        for(int s = 0; s < stroke1.size(); s++){
            WNBOI.LOGGER.info("stroke point: " + stroke1.get(s).x + ", " + stroke1.get(s).y);
        }
    }

}
