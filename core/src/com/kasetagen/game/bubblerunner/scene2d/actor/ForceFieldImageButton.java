package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.AnimatedActor;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 7/20/14
 * Time: 3:58 PM
 */
public class ForceFieldImageButton extends AnimatedActor {

    public ForceFieldType forceFieldType;


    public ForceFieldImageButton(float x, float y, float width, float height, Animation animation, ForceFieldType fft) {
        super(x, y, width, height, animation, 0f);
        this.forceFieldType = fft;
    }
}
