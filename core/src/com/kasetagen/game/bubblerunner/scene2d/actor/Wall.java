package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.kasetagen.game.bubblerunner.util.ForceFieldColorUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class Wall extends GenericActor {

    public ForceFieldType forceFieldType;
    public Vector2 velocity;

    public Wall(float x, float y, float width, float height, ForceFieldType ff, TextureRegion textureRegion){
        super(x, y, width, height, textureRegion, ForceFieldColorUtil.getColor(ff));
        this.forceFieldType = ff;
        this.velocity = new Vector2(1f, 1f);
    }

    public void setXVelocity(float x){
        velocity.x = x;
    }

    public void setYVelocity(float y){
        velocity.y = y;
    }


}
