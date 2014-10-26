package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.kasetagen.game.bubblerunner.util.AnimationUtil;


/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class Player extends com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericGroup {

    //private static final float ANIMATION_CYCLE_RATE = 1f/8f;

    private static final float FIELD_ADJUST = 20f;

    private static final int NUM_SHIELD_FRAMES = 2;

    public ForceFieldType forceFieldType;

    public int maxFields = 1;
    public int resourceUsage = 1;

    private Array<ForceField> fields;

    private float keyFrameTime = 0f;
    private Animation animation;
    private Animation deathAnimation;
    private Animation shieldAnimation;
    private boolean isDead = false;
    private boolean isShielding = false;
    private float shieldKeyFrameTime = 0f;
    private int shieldFramesRun = 0;

    private float runningWidth;
    private float dyingWidth;
    private float runningX;
    private float dyingX;

    private boolean isDyingForward = false;
    private float dyingVelocity = 30f;

    public Player(float x, float y, float width, float height, TextureAtlas atlas, String animationName){
        super(x, y, width, height, null, Color.BLACK);

        animation = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, atlas.findRegions(animationName));
        deathAnimation = new Animation(AnimationUtil.RUNNER_FIRE_CYCLE_RATE, atlas.findRegions("player/Female_Death"));
        textureRegion = animation.getKeyFrame(keyFrameTime);
        //TODO: Replace ShapeRendering with Animation
        forceFieldType = ForceFieldType.LIGHTNING;
        fields = new Array<ForceField>();

        //I don't want to do this if I can help it
        runningWidth = width;
        dyingWidth = 440f;

        runningX = x;
        dyingX = runningX - ((dyingWidth-runningWidth)/2);

    }

    public void setShieldingAnimation(Animation ani){
        shieldAnimation = ani;
        shieldKeyFrameTime = 0f;
    }

    public void startShield(){
        if(!isShielding){
            isShielding = true;
            shieldKeyFrameTime = 0f;
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
                shieldKeyFrameTime += delta;

                //We only want to run 2 frames of the shielding Animation that matchup to
                //  the running animations. So we keep track of the # of times we have swapped frames.
                TextureRegion prevFrame = shieldAnimation.getKeyFrame(prevKeyFrameTime, true);
                TextureRegion currentFrame = shieldAnimation.getKeyFrame(keyFrameTime, true);

                if(prevFrame != currentFrame){
                    shieldFramesRun++;
                }

                textureRegion = currentFrame;
                if(!textureRegion.isFlipX()){
                    textureRegion.flip(true, false);
                }
                if(shieldFramesRun >= NUM_SHIELD_FRAMES){
                    isShielding = false;
                    shieldFramesRun = 0;
                }
            }else{
                textureRegion = animation.getKeyFrame(keyFrameTime, true);
                if(!textureRegion.isFlipX()){
                    textureRegion.flip(true, false);
                }
            }

        }else{
            textureRegion = deathAnimation.getKeyFrame(keyFrameTime, false);
            if(!textureRegion.isFlipX()){
                textureRegion.flip(true, false);
            }
        }
    }

    public void setIsDead(boolean isDying){
        if(isDying){
            keyFrameTime = 0f;
            //THis gets ugly if we can prevent it we should
            setSize(dyingWidth, getHeight());
            setPosition(dyingX, getY());
        }else{
            setSize(runningWidth, getHeight());
            setPosition(runningX, getY());
        }
        isDead = isDying;
    }

    public void addField(ForceFieldType ff){

        if(fields.size == maxFields){
            //Remove the forcefield
            ForceField f = fields.get(0);
            this.removeActor(f);
            fields.removeIndex(0);
        }

        float radius = getWidth();
        float x = getX()-getWidth()/2;
        float y = getY();
        ForceField field = new ForceField(x, y, radius, ff);

        for(int i=fields.size-1;i>=0;i--){
            fields.get(i).targetRadius = fields.get(i).targetRadius + (FIELD_ADJUST);
        }

        this.addActor(field);
        fields.add(field);
    }

    public void addField(ForceFieldType ff, int index){

        float adjust = fields.size > 0 ? FIELD_ADJUST *fields.size - 1 : 0;
        float radius = getWidth() + adjust;
        float x = getX() - (getWidth()/2) - adjust;
        float y = getY() - adjust;
        ForceField field = new ForceField(x, y, radius, ff);
        this.addActor(field);
        fields.insert(index, field);
    }

    public void removeField(ForceField ff){
        this.removeActor(ff);
        fields.removeValue(ff, true);

        for(int i=0;i<fields.size;i++){
            fields.get(i).targetRadius = getWidth() + (FIELD_ADJUST * (fields.size-i-1));
        }
    }

    public void removeField(ForceFieldType ff){
        int removeIndex = -1;
        for(int i=0;i<fields.size;i++){
            if(ff.equals(fields.get(i).forceFieldType)){
                removeIndex = i;
                break;
            }
        }

        if(removeIndex >= 0){
            ForceField f = fields.get(removeIndex);
            this.removeActor(f);
            fields.removeIndex(removeIndex);
        }

        //When we remove a forcefield, we need to readjust all of the remaining
        //  fields into the proper positions.
        for(int i=0;i<fields.size;i++){
            fields.get(i).targetRadius = getWidth() + (FIELD_ADJUST * (fields.size-i-1));
        }
    }

    public void clearFields(){

        for(ForceField f:fields){
            this.removeActor(f);
        }
        fields.clear();
    }

    public ForceField getOuterForceField(){
        ForceField ff = null;
        if(fields.size > 0){
            ff = fields.get(0);
        }
        return ff;
    }

    @Override
    public void drawFull(Batch batch, float parentAlpha) {
        super.drawFull(batch, parentAlpha);
    }
}
