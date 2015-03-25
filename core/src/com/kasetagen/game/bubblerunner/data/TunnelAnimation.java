package com.kasetagen.game.bubblerunner.data;

import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 3/24/15
 * Time: 8:04 PM
 */
public class TunnelAnimation {

    public final Animation animation;
    public final boolean isSpecial;

    public TunnelAnimation(Animation ani, boolean isSpecial){
        this.animation = ani;
        this.isSpecial = isSpecial;
    }

}
