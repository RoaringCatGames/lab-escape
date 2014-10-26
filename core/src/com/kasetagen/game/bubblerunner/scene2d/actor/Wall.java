package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericActor;
import com.kasetagen.game.bubblerunner.util.ForceFieldColorUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class Wall extends GenericActor {

    private static final float WALL_CYCLE_RATE = 1f/8f;

    public ForceFieldType forceFieldType;
    public Vector2 velocity;
    private Animation animation;
    private float keyFrameTime = 0f;

    public Wall(float x, float y, float width, float height, ForceFieldType ff, TextureAtlas atlas, String animName){
        super(x, y, width, height, null, ForceFieldColorUtil.getColor(ff));

        animation = new Animation(WALL_CYCLE_RATE, atlas.findRegions(animName));
        this.forceFieldType = ff;
        this.velocity = new Vector2(1f, 1f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        keyFrameTime += delta;
        textureRegion = animation.getKeyFrame(keyFrameTime, true);
    }

    public void setXVelocity(float x){
        velocity.x = x;
    }

    public void setYVelocity(float y){
        velocity.y = y;
    }

    @Override
    protected void drawFull(Batch batch, float parentAlpha) {
        //super.drawFull(batch, parentAlpha);
        if(textureRegion != null){
            float regionW = textureRegion.getRegionWidth()/2;
            float regionH = textureRegion.getRegionHeight()/2;

            float midPoint = getX() + getWidth()/2;
            float regionX = midPoint - (regionW/2);

            batch.draw(textureRegion, regionX, 0f, regionW, regionH);
        }
    }
}
