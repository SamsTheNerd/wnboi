package com.samsthenerd.wnboi.screen;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.samsthenerd.wnboi.WNBOI;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/*
 * Any wheel context screen should be extended from here and customized to fit yours needs.
 * 
 * Defaults to an 8 sectioned wheel with standard minecraft-ish colors.
 * 
 */
@Environment(value=EnvType.CLIENT)
public class AbstractContextWheelScreen extends Screen{
    protected int numSections;

    // these are passed to the spoke renderers by default.
    protected double centerX;
    protected double centerY;
    protected double outerRadius;
    protected double innerRadius = 0;
    protected double gap = 10;
    protected double angleOffset = Math.PI * 0.5; // measured in radians off of x axis. default puts first section at 12 o'clock and goes clockwise

    protected int selectedSection = -1; // -1 means none selected
    // these determine when the mouse is considered to be in the section
    protected double lowerBoundRadius = 0;
    protected double upperBoundRadius = 0;

    double currentTime = 0; // updated on each render call
    double lastStateChange = 0; // set when selected or deselected
    int tooltipTickDelay = 30; // how many ticks to wait before showing the tooltip

    // use these to have the screen close when the key is released
    public KeyBinding keyBinding = null;
    public boolean requireKeydown = false;




    protected List<SpokeRenderer> spokeRenderers; // list so that we can reference them. 

    public AbstractContextWheelScreen(Text title, int numSecs){
        super(title);
        this.numSections = numSecs;
        spokeRenderers = new ArrayList<SpokeRenderer>();
    }

    public AbstractContextWheelScreen(){
        this(Text.of("Abstract Context Wheel"), 8);
    }

    public AbstractContextWheelScreen(Text title){
        this(title, 8);
    }

    @Override
    protected void init(){
        // WNBOI.LOGGER.info("made a new wheel screen with " + this.numSections + " sections called \"" + title.toString() + "\" | [width="+this.width+", height="+this.height+"]");
        addAllSections();
    }

    protected void initConsts(){
        this.centerX = this.width / 2.0;
        this.centerY = this.height / 2.0;

        this.outerRadius = this.height / 4.0;
        upperBoundRadius = outerRadius*1.1;
        lowerBoundRadius = outerRadius*0.1 > innerRadius ? outerRadius*0.1 : innerRadius;

        angleOffset = Math.PI * 0.5 + Math.PI / this.numSections; // so that the first spoke is centered at the top
    }

    @Override
    public void removed(){
        if(selectedSection != -1){
            triggerSpoke(selectedSection);
        }
        super.removed();
    }

    public boolean closeWheel(boolean requireSelection){
        WNBOI.LOGGER.info("closing wheel with closeWheel()");
        if(selectedSection != -1){
            triggerSpoke(selectedSection);
            if(this != null){
                WNBOI.LOGGER.info("actually closing");
                this.close();
            }
            return true;
        }
        if(!requireSelection && this != null){
            WNBOI.LOGGER.info("actually closing");
            this.close();
            return true;
        }
        return false;
    }

    // something thinks that this screen shouldn't be open, but it could be wrong so override this if it needs to ignore that close request.
    // e.g. if this wheel is handled by something other than a KeyboundItem it should override this to return false with reason == 0
    // reason:
    //   0: there is no KeyboundItem in the player's hands
    public void askToClose(int reason){
        this.closeWheel(true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button == 0){
            return closeWheel(true);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        WNBOI.LOGGER.info("pressed key " + keyCode);
        if(keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9){
            int index = keyCode - GLFW.GLFW_KEY_0 -1; // -1 so that 1 key triggers 0th index
            if(index == -1) index = 9;
            if(index < this.numSections){
                triggerSpoke(index);
                if(this != null){
                    this.close();
                }
                return true;
            } 
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(keyBinding != null && keyBinding.matchesKey(keyCode, scanCode) && requireKeydown){
            WNBOI.LOGGER.info("closing wheel with keyReleased()");
            this.closeWheel(false);
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }


    // do whatever logic you want it to do
    public void triggerSpoke(int index){
        // WNBOI.LOGGER.info("triggered spoke " + index);
        selectedSection = -1; // so that we don't recurse ourselves
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("triggered spoke " + index));
    }

    protected void addAllSections(){
        initConsts();
        spokeRenderers = new ArrayList<SpokeRenderer>(); // just to clear it
        for(int i = 0; i < this.numSections; i++){
            spokeRenderers.add(genSpokeRenderer(centerX, centerY, outerRadius, this.numSections, i));
            if(i == 0){
                spokeRenderers.get(0).labelItemStack = Items.DIAMOND.getDefaultStack();
                spokeRenderers.get(0).labelEntity = MinecraftClient.getInstance().player;
            } else {
                spokeRenderers.get(i).labelItemStack = Items.DIRT.getDefaultStack();
                ItemEntity ie = EntityType.ITEM.create(MinecraftClient.getInstance().world);
                ie.setStack(Items.DIRT.getDefaultStack());
                spokeRenderers.get(i).labelEntity = ie;
            }
            spokeRenderers.get(i).labelText = Text.of(Integer.toString(i));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        currentTime = MinecraftClient.getInstance().world.getTime() + delta;
        updateSelectedSection(mouseX, mouseY);
        super.render(matrices, mouseX, mouseY, delta);
        for(SpokeRenderer sr : spokeRenderers){
            sr.render(matrices, mouseX, mouseY, delta);
        }
        tryRenderTooltip(matrices, mouseX, mouseY);
    }

    public void tryRenderTooltip(MatrixStack matrices, int mouseX, int mouseY){
        if(selectedSection != -1 && (currentTime - lastStateChange) >= tooltipTickDelay){
            doRenderTooltip(matrices, mouseX, mouseY);
        }
    }

    // override this to change the tooltip
    protected void doRenderTooltip(MatrixStack matrices, int mouseX, int mouseY){
        this.renderTooltip(matrices, Text.of("tooltip for section " + selectedSection), mouseX, mouseY);
    }

    public boolean shouldPause() {
        return false;
    }

    protected SpokeRenderer genSpokeRenderer(double orX, double orY, double rad, int numSecs, int secIndex){
        return new SpokeRenderer(orX, orY, rad, numSecs, secIndex).setGap(gap).setInnerRadius(innerRadius).setAngleOffset(angleOffset);
    }

    // called on generated spoke renderers. override if you want to modify it.
    protected SpokeRenderer modifySpokeRenderer(SpokeRenderer sr){
        return sr;
    }

    protected void updateSelectedSection(int mouseX, int mouseY){
        int oldSelected = selectedSection;
        selectedSection = getSectionIndexFromMouse(mouseX, mouseY);
        if(oldSelected == selectedSection){
            return;
        }
        if(oldSelected != -1){
            spokeRenderers.get(oldSelected).unselect();;
        }
        if(selectedSection != -1){
            spokeRenderers.get(selectedSection).select();
        }
        lastStateChange = currentTime;
    }

    protected int getSectionIndexFromMouse(int mouseX, int mouseY){
        double diffX = mouseX - centerX;
        double diffY = mouseY - centerY;
        if(diffX == 0 && diffY == 0){
            // center
            return -1;
        }
        double dist = Math.sqrt(diffX * diffX + diffY * diffY);
        if(dist < lowerBoundRadius || dist > upperBoundRadius){
            // outside of the wheel
            return -1;
        }
        // otherwise inside the wheel
        double theta;
        if(diffX == 0){
            if(diffY > 0){
                theta = Math.PI * 0.5;
            }else{
                theta = Math.PI * 1.5;
            }
        } else {
            theta = Math.atan(diffY / diffX);
            if(diffX < 0){
                theta += Math.PI;
            }
        }
        return getSectionIndexFromAngle(theta);
    }

    protected int getSectionIndexFromAngle(double theta){
        theta += angleOffset;
        theta += Math.PI * 8; // make sure it's positive
        theta %= Math.PI * 2; // make sure it's less than 2pi
        return (int) Math.floor(theta / (2 * Math.PI) * numSections) ;
    }
}
