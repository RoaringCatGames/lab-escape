package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.AnimatedActor;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericActor;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:43 PM
 */
//public class Wall extends AnimatedActor {
//
//    public ForceFieldType forceFieldType;
//
//    public Wall(float x, float y, float width, float height,
//                Animation animation, float startKeyframe, ForceFieldType ff) {
//        super(x, y, width, height, animation, startKeyframe);
//        this.forceFieldType = ff;
//    }
//
//    public void setXVelocity(float x){
//        velocity.x = x;
//    }
//
//
//    @Override
//    protected void drawFull(Batch batch, float parentAlpha) {
//        super.drawFull(batch, parentAlpha);
//    }
//}


public class Wall{
    private AnimatedActor leftWall;
    private AnimatedActor rightWall;
    private GenericActor collidingActor;
    private AnimatedActor flourishing;
    public ForceFieldType forceFieldType;

    public Rectangle collider;

    private boolean isRemovable = false;

    public Wall(AnimatedActor leftWall, AnimatedActor rightWall, GenericActor collider, ForceFieldType fft) {
        this.forceFieldType = fft;
        this.leftWall = leftWall;
        this.rightWall = rightWall;
        this.collidingActor = collider;
        this.forceFieldType = fft;
        if(collidingActor != null){
            this.collider = collidingActor.collider;
        }
    }

    public void addFlourishing(AnimatedActor actor){
        this.flourishing = actor;
    }

    public float getX(){
        if(leftWall != null){
            return leftWall.getX();
        }else if(collidingActor != null){
            return collidingActor.getX();
        }else if(rightWall != null){
            return rightWall.getX();
        }else{
            return 0f;
        }
    }

    public float getRight(){
        if(rightWall != null){
            return rightWall.getRight();
        }else if(leftWall != null){
            return leftWall.getRight();
        }else{
            return getX();
        }
    }

    public float getWidth(){
        float width = 0f;
        if(leftWall != null){
            width += leftWall.getWidth();
        }
        if(rightWall != null){
            width += rightWall.getWidth();
        }
        return width;
    }

    public void setXVelocity(float velocity){
        if(leftWall != null){
            leftWall.velocity.x = velocity;
        }
        if(rightWall != null){
            rightWall.velocity.x = velocity;
        }
        if(collidingActor != null){
            collidingActor.velocity.x = velocity;
        }
        if(flourishing != null){
            flourishing.velocity.x = velocity;
        }
    }
    public void setState(String stateName, boolean shouldResetFrame){
        if(leftWall != null){
            leftWall.setState(stateName, shouldResetFrame);
        }
        if(rightWall != null){
            rightWall.setState(stateName, shouldResetFrame);
        }
        if(flourishing != null){
            flourishing.setState(stateName, shouldResetFrame);
        }
    }

    public void setIsLooping(boolean isLooping){
        if(leftWall != null){
            leftWall.setIsLooping(isLooping);
        }
        if(rightWall != null){
            rightWall.setIsLooping(isLooping);
        }
        if(flourishing != null){
            flourishing.setIsLooping(isLooping);
        }
    }

    public void addStateAnimation(String stateName, Animation leftAni, Animation rightAni, Animation flourish){
        if(leftAni != null){
            leftWall.addStateAnimation(stateName, leftAni);
        }
        if(rightAni != null){
            rightWall.addStateAnimation(stateName, rightAni);
        }
        if(flourish != null){
            flourishing.addStateAnimation(stateName, flourish);
        }
    }
    public String getCurrentState(){
        if(leftWall != null){
            return leftWall.getCurrentState();
        }else if(rightWall != null){
            return rightWall.getCurrentState();
        }else{
            return "DEFAULT";
        }

    }

    public boolean isRemovable(){

        return isRemovable ||
               ((leftWall == null || leftWall.isRemovable()) &&
                (rightWall == null || leftWall.isRemovable()) &&
                (collidingActor == null || leftWall.isRemovable()) &&
                (flourishing == null || flourishing.isRemovable()));
    }

    public void setIsRemovable(boolean isRemovable){
        if(leftWall != null){
            leftWall.setIsRemovable(isRemovable);
            leftWall = null;
        }
        if(rightWall != null){
            rightWall.setIsRemovable(isRemovable);
            rightWall = null;
        }
        if(collidingActor != null){
            collidingActor.setIsRemovable(isRemovable);
            collidingActor = null;
        }
        if(flourishing != null){
            flourishing.setIsRemovable(isRemovable);
            flourishing = null;
        }

        this.isRemovable = isRemovable;
    }
}