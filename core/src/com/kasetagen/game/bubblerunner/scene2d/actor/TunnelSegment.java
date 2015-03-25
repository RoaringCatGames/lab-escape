package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.AnimatedActor;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 3/24/15
 * Time: 8:02 PM
 */
public class TunnelSegment extends AnimatedActor {

    private boolean isSpecial = false;
    public TunnelSegment(float x, float y, float width, float height, Animation animation, float startKeyframe) {
        super(x, y, width, height, animation, startKeyframe);
    }

    public void setSpecial(boolean isSpecial){
        this.isSpecial = isSpecial;
    }

    public boolean isSpecial(){
        return isSpecial;
    }

}
