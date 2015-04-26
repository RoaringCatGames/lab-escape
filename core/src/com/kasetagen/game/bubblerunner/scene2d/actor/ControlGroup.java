package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
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


    private static final float BUTTON_WIDTH = 260f/2f;
    private static final float BUTTON_HEIGHT = 475f/2f;
    private static final float BUTTON_PADDING = 10f;

    private static final float BAR_START = BUTTON_WIDTH * 4f;

    private static int OVERHEAT_POINT = 40;

    //public ObjectMap<ForceFieldType, Integer> resourceLevels;
    private int buttonCount = 0;

    private int heatScore = 0;

    private TextureRegion energyBar = null;

    private Array<ForceFieldImageButton> buttons;

    public ControlGroup(float x, float y, float width, float height, Color color){
        super(x, y, width, height, null, color);
        buttons = new Array<ForceFieldImageButton>();
    }

    /**
     *  Returns the target position of a butotn at the given index.
     *
     * @param buttonIndex - 1-based button index
     * @return
     */
    private Vector2 getButtonPosition(int buttonIndex){
        return new Vector2(0f, (BUTTON_WIDTH + BUTTON_PADDING) * buttonIndex);
    }

    public void setPressed(ForceFieldType fft){
        for(ForceFieldImageButton b:buttons){
            if(b.forceFieldType == fft){
                b.setState("PRESSED", true);
            }
        }
    }

    public void addButton(ForceFieldType fft, EventListener listener, Animation defaultAni, Animation pressedAni, float rotation){
        Vector2 buttonPos = getButtonPosition(buttonCount);
        Gdx.app.log("BUTTON POSITION", "X: " + buttonPos.x + " Y: " + buttonPos.y);
        ForceFieldImageButton btn = new ForceFieldImageButton(buttonPos.x, buttonPos.y, BUTTON_WIDTH, BUTTON_HEIGHT, defaultAni, fft);
        btn.setRotation(rotation);
        btn.addStateAnimation("PRESSED", pressedAni);
        btn.addListener(listener);
        btn.setIsLooping(false);
        addActor(btn);

        buttons.add(btn);

        buttonCount++;
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
