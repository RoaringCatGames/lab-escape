package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.AnimatedActor;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericActor;
import com.kasetagen.game.bubblerunner.util.ForceFieldColorUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:43 PM
 */
public class Wall extends AnimatedActor {

    public ForceFieldType forceFieldType;

    public Wall(float x, float y, float width, float height, Animation animation, float startKeyframe, ForceFieldType ff) {
        super(x, y, width, height, animation, startKeyframe);
        this.forceFieldType = ff;
    }

    public void setXVelocity(float x){
        velocity.x = x;
    }


    @Override
    protected void drawFull(Batch batch, float parentAlpha) {
        super.drawFull(batch, parentAlpha);
    }
}
