package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.decals.GroupStrategy;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.kasetagen.game.bubblerunner.scene2d.BubbleRunner3dStage;
import com.kasetagen.game.bubblerunner.scene2d.BubbleRunnerStage;
import com.kasetagen.game.bubblerunner.util.AnimationUtil;


/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class Player3d extends Entity {

    //private static final float ANIMATION_CYCLE_RATE = 1f/8f;

    private static final float FIELD_ADJUST = 20f;

    private static final int NUM_SHIELD_FRAMES = 2;

    public ForceFieldType forceFieldType;

    public int maxFields = 1;
    public int resourceUsage = 1;

    private Array<ForceField> fieldsArray;

    private float keyFrameTime = 0f;
    private Animation animation;
    private Animation deathAnimation;
    private Animation shieldAnimation;
    private boolean isDead = false;
    private boolean isShielding = false;
    private float shieldKeyFrameTime = 0f;
    private int shieldFramesRun = 0;
    
    protected TextureRegion textureRegion;
    
    private Decal decal;
    private DecalBatch decalBatch;
    private GroupStrategy groupStrategy;
    protected PerspectiveCamera cam;
    protected BubbleRunner3dStage stage;
    protected Array<Decal> shieldArray = new Array<Decal>();

    public Player3d(float x, float y, float width, float height, TextureAtlas atlas, String animationName, BubbleRunner3dStage stage){
        super(x, y, width, height, Color.BLACK);
        
        this.stage = stage;
    	
    	animation = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, atlas.findRegions(animationName));
        deathAnimation = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, atlas.findRegions("explosion/explosion"));
        textureRegion = animation.getKeyFrame(keyFrameTime);
        //TODO: Replace ShapeRendering with Animation
        forceFieldType = ForceFieldType.LIGHTNING;
        fieldsArray = new Array<ForceField>();
        
        cam = stage.get3dCamera();
        
        groupStrategy = new CameraGroupStrategy(stage.get3dCamera());
        decal = Decal.newDecal(10, 10, textureRegion, true);
        decal.setPosition(10, 0, 0);
        decalBatch = new DecalBatch(groupStrategy);
    }
    
    public Decal getDecal(){
    	return decal;
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

    public void act(float delta) {
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
            textureRegion = deathAnimation.getKeyFrame(keyFrameTime, true);
        }
        
        decal.lookAt(cam.position.cpy(), cam.up.cpy().nor());
        decal.setTextureRegion(textureRegion);
        
    	for(ForceField f:fieldsArray){
    		f.act(delta);
    	}
    }
    
    public void draw(){
    	
    	for(ForceField f:fieldsArray){
    		//f.draw();
    	}
    	
    	stage.getDecalBatch().add(decal);
    }

    public void setIsDead(boolean isDying){
        if(isDying){
            keyFrameTime = 0f;
        }
        isDead = isDying;
    }

    public void addField(ForceFieldType ff){

        if(fieldsArray.size == maxFields){
            //Remove the forcefield
            ForceField f = fieldsArray.get(0);
            fieldsArray.removeIndex(0);
        }

        float radius = getWidth();
        float x = getX()-getWidth()/2;
        float y = getY();
        ForceField field = new ForceField(x, y, radius, ff);

        for(int i=fieldsArray.size-1;i>=0;i--){
            fieldsArray.get(i).targetRadius = fieldsArray.get(i).targetRadius + (FIELD_ADJUST);
        }

        fieldsArray.add(field);
    }

    public void addField(ForceFieldType ff, int index){

        float adjust = fieldsArray.size > 0 ? FIELD_ADJUST *fieldsArray.size - 1 : 0;
        float radius = getWidth() + adjust;
        float x = getX() - (getWidth()/2) - adjust;
        float y = getY() - adjust;
        ForceField field = new ForceField(x, y, radius, ff);
        fieldsArray.insert(index, field);
    }

    public void removeField(ForceField ff){
        fieldsArray.removeValue(ff, true);
        //Gdx.app.log("PLAYER", "Fields Size: " + fields.size);

        for(int i=0;i<fieldsArray.size;i++){
            fieldsArray.get(i).targetRadius = getWidth() + (FIELD_ADJUST * (fieldsArray.size-i-1));
        }
    }

    public void removeField(ForceFieldType ff){
        int removeIndex = -1;
        for(int i=0;i<fieldsArray.size;i++){
            if(ff.equals(fieldsArray.get(i).forceFieldType)){
                removeIndex = i;
                break;
            }
        }

        if(removeIndex >= 0){
            ForceField f = fieldsArray.get(removeIndex);
            fieldsArray.removeIndex(removeIndex);
        }

        //When we remove a forcefield, we need to readjust all of the remaining
        //  fields into the proper positions.
        for(int i=0;i<fieldsArray.size;i++){
            fieldsArray.get(i).targetRadius = getWidth() + (FIELD_ADJUST * (fieldsArray.size-i-1));
        }
    }

    public void clearFields(){

//        for(ForceField f:fieldsArray){
//            this.removeActor(f);
//        }
        fieldsArray.clear();
    }

    public ForceField getOuterForceField(){
        ForceField ff = null;
        if(fieldsArray.size > 0){
            ff = fieldsArray.get(0);
        }
        return ff;
    }

//    @Override
//    public void drawFull(Batch batch, float parentAlpha) {
//        if(!isDead){
//        super.drawFull(batch, parentAlpha);
//        }else{
//            batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth()*2, getHeight()*1.5f, getScaleX()*2, getScaleY()*1.5f, getRotation());
//        }
//    }
}
