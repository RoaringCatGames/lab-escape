package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.AnimatedActor;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 6/1/14
 * Time: 7:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForceField extends AnimatedActor {

    public ForceFieldType forceFieldType;
    public float targetX;
    public float tweenSpeed;
    //public float radius;

    public ForceField(float x, float y, float width, float height, Animation ani, ForceFieldType ff){
        super(x, y, width, height, ani, 0f);

        forceFieldType = ff;
        targetX = x;
        this.isLooping = false;
        tweenSpeed = width*2;
        //this.radius = radius;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        //Manually "Tween" our position
        if(targetX > getX()){
            setX(getX() + (delta*tweenSpeed));
            if(getX() > targetX){
                setX(targetX);
            }
        }else if(targetX < getX()){
            setX(getX() - (delta*tweenSpeed));
            if(getX() < targetX){
                setX(targetX);
            }
        }

        if(textureRegion != null && !textureRegion.isFlipX()){
            textureRegion.flip(true, false);
        }
    }
}
