package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 6/22/14
 * Time: 11:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class ControlGroup extends GenericGroup{

    private static final float BUTTON_WIDTH = 150f;
    private static final float BUTTON_HEIGHT = 150f;
    private static final float BUTTON_PADDING = 10f;

    private static final float BAR_START = BUTTON_WIDTH * 3.5f;

    private static int RESOURCE_MAX = 20;
    private static int OVERHEAT_POINT = 40;

    //public ObjectMap<ForceFieldType, Integer> resourceLevels;
    private int buttonCount = 0;

    private int heatScore = 0;


    private Array<ForceFieldImageButton> buttons;

    private float getButtonX(int buttonPos){
        return (BUTTON_WIDTH + BUTTON_PADDING) * buttonPos;
    }

    public ControlGroup(float x, float y, float width, float height, Color color){
        super(x, y, width, height, color);

//        resourceLevels = new ObjectMap<ForceFieldType, Integer>();
//        resourceLevels.put(ForceFieldType.LASER, RESOURCE_MAX);
//        resourceLevels.put(ForceFieldType.LIGHTNING, RESOURCE_MAX);
//        resourceLevels.put(ForceFieldType.PLASMA, RESOURCE_MAX);

        buttons = new Array<ForceFieldImageButton>();
    }

    public void addButton(Drawable up, Drawable down, Drawable over, EventListener listener, boolean isVisible, ForceFieldType fft){
        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.imageUp = up;
        buttonStyle.imageDown = down;
        buttonStyle.imageOver = over;

        ForceFieldImageButton button = new ForceFieldImageButton(buttonStyle, fft);
        button.setPosition(getButtonX(buttonCount), 0);
        button.setWidth(BUTTON_WIDTH);
        button.setHeight(BUTTON_HEIGHT);
        button.addListener(listener);
        button.setVisible(isVisible);
        addActor(button);

        buttons.add(button);

        buttonCount++;
    }

    public int getResourceLevel(ForceFieldType fft){
        return heatScore;
        //return resourceLevels.get(fft);
    }

    public int getHeatMax(){
        return OVERHEAT_POINT;
    }

    public void incrementHeat(int increment){
        heatScore += increment;
    }

    public void restoreAllResourceLevels(){
        heatScore = 0;
    }

    @Override
    protected void drawAfter(Batch batch, float parentAlpha) {
        super.drawAfter(batch, parentAlpha);
        batch.end();
        batch.begin();
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float barTotalLength = getWidth() - BAR_START;
        float barLength = barTotalLength*((float)heatScore/(float)OVERHEAT_POINT);
        Color c = heatScore >= OVERHEAT_POINT ? Color.RED : Color.ORANGE;
        debugRenderer.setColor(c);
        debugRenderer.rect(BAR_START, getY(), barLength, getHeight());
        //End our shapeRenderer, flush the batch, and re-open it for future use as it was open
        // coming in.
        debugRenderer.end();
        batch.end();
        batch.begin();

    }
}
