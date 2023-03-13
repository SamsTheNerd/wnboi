package com.samsthenerd.wnboi.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

/*
 * Any wheel context screen should be extended from here and customized to fit yours needs.
 * 
 * Defaults to an 8 sectioned wheel with standard minecraft-ish colors.
 * 
 */
@Environment(value=EnvType.CLIENT)
public class AbstractContextWheelScreen extends Screen{
    protected int numSections;
    

    public AbstractContextWheelScreen(int numSecs){
        super(null);
        this.numSections = numSecs;
        this.addDrawable(new DefaultWheelSectionRenderer(this.width / 2, this.height / 2, this.height/4, numSecs, 0));
    }

    public AbstractContextWheelScreen(){
        this(8);
    }

    /*
     * Just renders a plain looking section.
     */
    @Environment(value=EnvType.CLIENT)
    public static class DefaultWheelSectionRenderer implements Drawable{
        protected double radius; // radius of the wheel
        protected int originX; // center of the wheel
        protected int originY;
        protected int sections; // number of sections, so we know arc radius

        protected int sectionIndex; // so we know which one we're actually trying to render

        DefaultWheelSectionRenderer(int orX, int orY, double rad, int numSecs, int secIndex){
            this.originX = orX;
            this.originY = orY;
            this.radius = rad;
            this.sections = numSecs;
            this.sectionIndex = secIndex;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta){
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLES , VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(originX, originY, 0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(originX + radius * Math.cos(2 * Math.PI * sectionIndex / sections), originY + radius * Math.sin(2 * Math.PI * sectionIndex / sections), 0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(originX + radius * Math.cos(2 * Math.PI * (sectionIndex + 1) / sections), originY + radius * Math.sin(2 * Math.PI * (sectionIndex + 1) / sections), 0).color(0, 0, 0, 255).next();
            BufferRenderer.drawWithShader(bufferBuilder.end());
            return;
        }


    }
}
