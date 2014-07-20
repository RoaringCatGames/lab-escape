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

    public ObjectMap<ForceFieldType, Integer> resourceLevels;
    private int buttonCount = 0;

    private int heatScore = 0;


    private Array<ForceFieldImageButton> buttons;

    private float getButtonX(int buttonPos){
        return (BUTTON_WIDTH + BUTTON_PADDING) * buttonPos;
    }

    public ControlGroup(float x, float y, float width, float height, Color color){
        super(x, y, width, height, color);

        resourceLevels = new ObjectMap<ForceFieldType, Integer>();
        resourceLevels.put(ForceFieldType.LASER, RESOURCE_MAX);
        resourceLevels.put(ForceFieldType.LIGHTNING, RESOURCE_MAX);
        resourceLevels.put(ForceFieldType.PLASMA, RESOURCE_MAX);

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

    public void incrementHeat(int increment){
        heatScore += increment;
    }

    public void restoreAllResourceLevels(){
        updateResource(ForceFieldType.LIGHTNING, RESOURCE_MAX);
        updateResource(ForceFieldType.LASER, RESOURCE_MAX);
        updateResource(ForceFieldType.PLASMA, RESOURCE_MAX);
    }

    @Override
    protected void drawAfter(Batch batch, float parentAlpha) {
        super.drawAfter(batch, parentAlpha);
        batch.end();
        batch.begin();
        Gdx.gl20.glLineWidth(1f);
        //Set the projection matrix, and line shape
        //debugRenderer.setProjectionMatrix(getStage().getCamera().combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //Draw the bounds of the actor as a box
        Color c = getColor() != null ? getColor() : Color.WHITE;
        debugRenderer.setColor(c);
        for(ForceFieldImageButton b:buttons){
            float currentResource = resourceLevels.get(b.forceFieldType);
            float height = b.getHeight() * (currentResource/RESOURCE_MAX);
            Gdx.app.log("CONTROLS", "Current Resource: " + currentResource + " Height: " + height);
            debugRenderer.rect(b.getX()+(b.getWidth()/4), b.getY(), b.getWidth()/2, height);
        }

        float barTotalLength = getWidth() - BAR_START;
        float barLength = barTotalLength*(heatScore/barTotalLength);
        Gdx.app.log("CONTROLS", "Bar Total: " + barTotalLength + " Bar Length: " + barLength);

        setColor(Color.RED);
        debugRenderer.rect(BAR_START, getY(), barLength, getHeight());
        //End our shapeRenderer, flush the batch, and re-open it for future use as it was open
        // coming in.
        debugRenderer.end();
        batch.end();
        batch.begin();

    }
}
