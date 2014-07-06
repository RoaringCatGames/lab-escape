package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
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

    private static int RESOURCE_MAX = 20;

    public ObjectMap<ForceFieldType, Integer> resourceLevels;
    private int buttonCount = 0;

    private float getButtonX(int buttonPos){
        return (BUTTON_WIDTH + BUTTON_PADDING) * buttonPos;
    }

    public ControlGroup(float x, float y, float width, float height, Color color){
        super(x, y, width, height, color);

        resourceLevels = new ObjectMap<ForceFieldType, Integer>();
        resourceLevels.put(ForceFieldType.LASER, RESOURCE_MAX);
        resourceLevels.put(ForceFieldType.LIGHTNING, RESOURCE_MAX);
        resourceLevels.put(ForceFieldType.PLASMA, RESOURCE_MAX);
    }

    public void addButton(Drawable up, Drawable down, Drawable over, EventListener listener, boolean isVisible){
        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.imageUp = up;
        buttonStyle.imageDown = down;
        buttonStyle.imageOver = over;

        ImageButton button = new ImageButton(buttonStyle);
        button.setPosition(getButtonX(buttonCount), 0);
        button.setWidth(BUTTON_WIDTH);
        button.setHeight(BUTTON_HEIGHT);
        button.addListener(listener);
        button.setVisible(isVisible);
        addActor(button);

        buttonCount++;
    }

    public int getResourceLevel(ForceFieldType fft){
        return resourceLevels.get(fft);
    }

    public void updateResource(ForceFieldType fft, int increment){
        int val = resourceLevels.get(fft);
        if(val < RESOURCE_MAX || (increment < 0 && val >= increment)){
            val += increment;
            int newVal = val <= RESOURCE_MAX ? val : RESOURCE_MAX;
            resourceLevels.put(fft, newVal);
        }
    }

}
