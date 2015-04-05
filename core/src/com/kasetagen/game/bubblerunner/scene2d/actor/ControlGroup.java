package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.AnimatedActor;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericGroup;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 6/22/14
 * Time: 11:13 AM
 */
public class ControlGroup extends GenericGroup {


    private static final float BUTTON_WIDTH = 260f/2f;//150f;
    private static final float BUTTON_HEIGHT = 475f/2f;//150f;
    private static final float BUTTON_PADDING = 10f;

    private static final float BAR_START = BUTTON_WIDTH * 4f;

    private static int RESOURCE_MAX = 20;
    private static int OVERHEAT_POINT = 40;

    //public ObjectMap<ForceFieldType, Integer> resourceLevels;
    private int buttonCount = 0;

    private int heatScore = 0;

    private TextureRegion energyBar = null;

    private Array<ForceFieldImageButton> buttons;

    private float getButtonX(int buttonPos){
        return (BUTTON_WIDTH + BUTTON_PADDING) * buttonPos;
    }

    public ControlGroup(float x, float y, float width, float height, Color color){
        super(x, y, width, height, null, color);
        buttons = new Array<ForceFieldImageButton>();
    }

    public void setPressed(ForceFieldType fft){
        for(ForceFieldImageButton b:buttons){
            if(b.forceFieldType == fft){
                b.setState("PRESSED", true);
            }
        }
    }

    public void addButton(ForceFieldType fft, EventListener listener, Animation defaultAni, Animation pressedAni){
        ForceFieldImageButton btn = new ForceFieldImageButton(getButtonX(buttonCount), -30f, BUTTON_WIDTH, BUTTON_HEIGHT, defaultAni, fft);
        btn.addStateAnimation("PRESSED", pressedAni);
        btn.addListener(listener);
        btn.setIsLooping(false);
        addActor(btn);

        buttons.add(btn);

        buttonCount++;
    }

    public void setEnergyBar(TextureRegion barTexture){
        energyBar = barTexture;
    }


    public int getResourceLevel(){
        return heatScore;
    }

    public int getResourceLevel(ForceFieldType fft){
        return heatScore;
    }

    public int getHeatMax(){
        return OVERHEAT_POINT;
    }

    public void incrementHeat(int increment){
        heatScore += increment;
        if(heatScore < 0){
            heatScore = 0;
        }
    }

    public void restoreAllResourceLevels(){
        heatScore = 0;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        //Reset all of our animations
        for(ForceFieldImageButton b:buttons){
            if("PRESSED".equals(b.getCurrentState()) && b.isAnimationComplete()){
                b.setState(AnimatedActor.DEFAULT_STATE, true);
            }
        }
    }

    @Override
    protected void drawAfter(Batch batch, float parentAlpha) {
        super.drawAfter(batch, parentAlpha);

        if(energyBar != null){
            float barTotalLength = getWidth() - BAR_START;
            float barLength = barTotalLength*((float)heatScore/(float)OVERHEAT_POINT);
            float yPos = getY()+(getHeight()/4)+4;
            float height = (getHeight()/2)-6;
            Color c = heatScore >= OVERHEAT_POINT ? Color.RED : Color.ORANGE;


            batch.end();
            batch.begin();
            debugRenderer.setProjectionMatrix(getStage().getCamera().combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
            debugRenderer.setColor(c);
            debugRenderer.rect(BAR_START, yPos, barLength, height);
            //End our shapeRenderer, flush the batch, and re-open it for future use as it was open
            // coming in.
            debugRenderer.end();
            batch.end();
            batch.begin();
            batch.draw(energyBar, BAR_START, getY()+(getHeight()/4), getWidth()-BAR_START, getHeight()/2);
        }
    }
}
