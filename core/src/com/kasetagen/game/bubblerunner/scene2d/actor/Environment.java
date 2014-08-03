package com.kasetagen.game.bubblerunner.scene2d.actor;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 10:14 PM
 * To change this template use File | Settings | File Templates.
 */
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.kasetagen.game.bubblerunner.util.ForceFieldColorUtil;

public class Environment extends GenericActor {
	private static final float WALL_CYCLE_RATE = 1f/8f;

    public ForceFieldType forceFieldType;
    public Vector2 velocity;
    private Animation animation;
    private float keyFrameTime = 0f;
    
    public Environment(float x, float y, float width, float height,
			TextureRegion textureRegion, Color color) {
		super(x, y, width, height, textureRegion, color);
		
		this.velocity = new Vector2(1f, 1f);
	}

	@Override
    public void act(float delta) {
        super.act(delta);
    }

    public void setXVelocity(float x){
        velocity.x = x;
    }

    public void setYVelocity(float y){
        velocity.y = y;
    }


}
