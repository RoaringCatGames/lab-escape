package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kasetagen.game.bubblerunner.util.AnimationUtil;


/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class Player extends com.kasetagen.engine.gdx.scenes.scene2d.actors.AnimatedActor {

    private static final int NUM_SHIELD_FRAMES = 2;

    private float keyFrameTime = 0f;
    private Animation animation;
    private Animation deathAnimation;
    private Animation shieldAnimation;
    private boolean isDead = false;
    private boolean isShielding = false;
    private int shieldFramesRun = 0;

    public Player(float x, float y, float width, float height, TextureAtlas atlas, String animationName){
        super(x, y, width, height, null, 0);

        animation = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, atlas.findRegions(animationName));
        deathAnimation = new Animation(AnimationUtil.RUNNER_FIRE_CYCLE_RATE, atlas.findRegions("player/Wall"));
        textureRegion = animation.getKeyFrame(keyFrameTime);
    }

    public void setShieldingAnimation(Animation ani){
        shieldAnimation = ani;
    }

    public void startShield(){
        if(!isShielding){
            isShielding = true;
        }
    }

    public void resetAnimation(Animation ani){
        animation = ani;
        keyFrameTime = 0f;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float prevKeyFrameTime = keyFrameTime;

        keyFrameTime += delta;
        if(!isDead){
            if(isShielding && shieldAnimation != null){

                //We only want to run 2 frames of the shielding Animation that matchup to
                //  the running animations. So we keep track of the # of times we have swapped frames.
                TextureRegion prevFrame = shieldAnimation.getKeyFrame(prevKeyFrameTime, true);
                TextureRegion currentFrame = shieldAnimation.getKeyFrame(keyFrameTime, true);

                if(prevFrame != currentFrame){
                    shieldFramesRun++;
                }

                textureRegion = currentFrame;
                if(shieldFramesRun >= NUM_SHIELD_FRAMES){
                    isShielding = false;
                    shieldFramesRun = 0;
                }
            }else{
                textureRegion = animation.getKeyFrame(keyFrameTime, true);
            }

        }else{
            textureRegion = deathAnimation.getKeyFrame(keyFrameTime, false);
        }

        if(!textureRegion.isFlipX()){
            textureRegion.flip(true, false);
        }
    }

    public void setIsDead(boolean isDying){
        if(isDying){
            keyFrameTime = 0f;
        }
        isDead = isDying;
    }

    @Override
    public void drawFull(Batch batch, float parentAlpha) {
        super.drawFull(batch, parentAlpha);
    }
}
