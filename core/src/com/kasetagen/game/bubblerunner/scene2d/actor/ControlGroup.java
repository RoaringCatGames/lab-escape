package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
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


    private static final float BUTTON_HEIGHT = 260f/2f;
    private static final float BUTTON_WIDTH = 475f/2f;
    private static final float BUTTON_PADDING = 30f;
    private int buttonCount = 0;

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
        return new Vector2(0f, (BUTTON_HEIGHT + BUTTON_PADDING) * buttonIndex);
    }

    public void setPressed(ForceFieldType fft){
        for(ForceFieldImageButton b:buttons){
            if(b.forceFieldType == fft){
                b.setState("PRESSED", true);
            }
        }
    }

    public void addButton(ForceFieldType fft, EventListener listener, Animation defaultAni, Animation pressedAni, float rotation, boolean isFlipped){
        Vector2 buttonPos = getButtonPosition(buttonCount);
        ForceFieldImageButton btn = new ForceFieldImageButton(buttonPos.x, buttonPos.y, BUTTON_WIDTH, BUTTON_HEIGHT, defaultAni, fft);
        btn.addStateAnimation("PRESSED", pressedAni);
        btn.flipTextureRegion(isFlipped, false);
        btn.setRotation(rotation);
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
}
