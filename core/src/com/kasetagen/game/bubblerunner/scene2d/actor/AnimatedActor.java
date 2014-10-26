package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericActor;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 10/21/14
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnimatedActor extends GenericActor {

    public Animation animation;

    private float keyFrameTime = 0f;

    private int targetKeyFrame = -1;

    public void setTargetKeyFrame(int keyFrame){
        if(animation != null && keyFrame < animation.getKeyFrames().length ){
            targetKeyFrame = keyFrame;
        }
    }

    public AnimatedActor(float x, float y, float width, float height, Animation animation, float startKeyframe){
        super(x, y, width, height, null, Color.GREEN);
        keyFrameTime = startKeyframe;
        this.animation = animation;

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(animation != null){
            if(targetKeyFrame < 0){
                textureRegion = animation.getKeyFrame(keyFrameTime, true);
                keyFrameTime += delta;
            }else{
                textureRegion = animation.getKeyFrames()[targetKeyFrame];
            }
        }
    }

}
