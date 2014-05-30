package com.kasetagen.game.bubblerunner.scene2d.actor;

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

    public Wall(float x, float y, float width, float height, ForceFieldType ff){
        super(x, y, width, height, ForceFieldColorUtil.getColor(ff));
        this.forceFieldType = ff;
    }
}
