package com.samsthenerd.wnboi.utils;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL30;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.samsthenerd.wnboi.WNBOI;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper.Argb;
import net.minecraft.util.math.Vec3d;

/*
 * just some functions for rendering math.
 * mostly point calculations 
 */
public class RenderUtils {
    
    // returns points that make up the outline of an arc.
    // returns numDivisons+1 points
    // angle starts at 0 and goes counter clockwise to 2pi
    // invert will give you a concave arc that still goes counter clockwise
    public static List<Vec3d> calcArcPoints(int numDivisions, double radius, double startAngle, double endAngle, boolean invert){
        List<Vec3d> points = new ArrayList<Vec3d>();
        double angle = startAngle;
        double angleStep = (endAngle - startAngle) / numDivisions;
        Vec3d posCorrection = new Vec3d(0, 0, 0); // normally nothing, but if we're inverted we need to correct the position
        if(invert){ // need to start at other side and go clockwise
            angle = endAngle+Math.PI;
            angleStep *= -1;
            posCorrection = new Vec3d(radius*(Math.cos(startAngle)-Math.cos(endAngle+Math.PI)), 
                radius*(Math.sin(startAngle)-Math.sin(endAngle+Math.PI)), 0);
        }
        for(int i = 0; i <= numDivisions; i++){
            points.add(new Vec3d(radius * Math.cos(angle), radius * Math.sin(angle), 0).add(posCorrection));
            angle += angleStep;
        }
        return points;
    }

    public static List<Vec3d> calcArcPoints(int numDivisions, double radius, double startAngle, double endAngle){
        return calcArcPoints(numDivisions, radius, startAngle, endAngle, false);
    }

    // used for the polygon curve type described below
    public static List<Vec3d> calcPolyPoints(int numDivisions, double curveRadius, double startAngle, double endAngle, boolean invert){
        Vec3d pointA = new Vec3d(curveRadius * Math.cos(startAngle), curveRadius * Math.sin(startAngle), 0);
        Vec3d pointB = new Vec3d(curveRadius * Math.cos(endAngle), curveRadius * Math.sin(endAngle), 0);
        Vec3d center = pointB.subtract(pointA).multiply(0.5).add(pointA); 
        Vec3d adjA = pointA.subtract(center); 
        double aAngle = Math.atan2(adjA.y, adjA.x);
        double bAngle = aAngle + Math.PI;
        double adjRad = adjA.length();
        // gets points as if they're on a circle centered between our endpoints.
        List<Vec3d> polyPoints = calcArcPoints(numDivisions, adjRad, aAngle, bAngle, invert); 
        // all are relative to center so add it back
        for(int i = 0; i < polyPoints.size(); i++){
            polyPoints.set(i, polyPoints.get(i).add(center));
        }
        return polyPoints;

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

    // renders the item icon with more options available
    public static void renderItemIcon(MatrixStack matrices, ItemStack stack, int x, int y, int argb){
        setupTransparencyFrameBuffer();
        MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(stack, x, y);
        drawTransparencyBuffer(matrices, argb);
    }

    // inspired by https://github.com/wisp-forest/owo-lib/blob/1.19.3/src/main/java/io/wispforest/owo/ui/container/RenderEffectWrapper.java

    public static int initialFrameBuffer;
    public static Framebuffer transFrameBuffer; 

    // call this if you want to draw transparency
    public static void setupTransparencyFrameBuffer(){
        Window window = MinecraftClient.getInstance().getWindow();
        initialFrameBuffer = GlStateManager.getBoundFramebuffer();
        transFrameBuffer = new SimpleFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC);
        transFrameBuffer.setClearColor(0, 0, 0, 0);
        transFrameBuffer.beginWrite(false);
    }

    public static void drawTransparencyBuffer(MatrixStack matrices, int argb){
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, initialFrameBuffer);
        RenderSystem.setShaderColor(Argb.getRed(argb)/255f, Argb.getGreen(argb)/255f, Argb.getBlue(argb)/255f, Argb.getAlpha(argb)/255f);
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, transFrameBuffer.getColorAttachment());

        Window window = MinecraftClient.getInstance().getWindow();
        // DrawableHelper.drawTexture(matrices, 0, 0, 0, window.getScaledHeight(), window.getWidth(), window.getHeight(), window.getScaledWidth(), window.getScaledHeight());
        DrawableHelper.drawTexture(matrices, 0, 0,
            window.getScaledWidth(), window.getScaledHeight(),
            0, transFrameBuffer.textureHeight,
            transFrameBuffer.textureWidth, -transFrameBuffer.textureHeight,
            transFrameBuffer.textureWidth, transFrameBuffer.textureHeight);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1,1,1,1);
    }



    // different types of built-in curves. can support up to 16 different types.
    // if you want to use your own curve it's probably best to ignore this and just override the getCurve methods directly

    /* pass curve options without having a whole other class for it
        curve types are just 4 bit values that map to default curve types:
            -arc, default, approximates a circle
            -polygon, approximates half of a polygon based on number of divisions, so 1 is flat, 
                2 is a triangle (half of a square), 3 is a trapezoid (half of a hexagon),
                 .., n divisons is half of a 2n sided polygon
        invert options are just booleans that invert the curves
    */
    public static int buildCurveOptions(int outerCurve, int innerCurve, boolean invertOuter, boolean invertInner){
        int options = 0;
        options += outerCurve;
        options += outerCurve << 4;
        options += (invertOuter ? 1 : 0) << 8;
        options += (invertInner ? 1 : 0) << 9;

        return options;
    }

    public static int getOuterCurve(int options){
        return options & 0xF;
    }

    public static int getInnerCurve(int options){
        return (options >>> 4) & 0xF;
    }

    public static boolean getInvertOuter(int options){
        return (options >>> 8) % 2 == 1;
    }

    public static boolean getInvertInner(int options){
        return (options >>> 9) % 2 == 1;
    }

    public static int setOuterCurve(int options, int curve){
        return (options & 0xFFF0) + curve;
    }

    public static int setInnerCurve(int options, int curve){
        return (options & 0xFF0F) + (curve << 4);
    }

    public static int setInvertOuter(int options, boolean invert){
        return (options & 0xFEFF) + ((invert ? 1 : 0) << 8);
    }

    public static int setInvertInner(int options, boolean invert){
        return (options & 0xFDFF) + ((invert ? 1 : 0) << 9);
    }


}
