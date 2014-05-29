package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;


/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class Player extends GenericActor {

    public ForceField forceField;

    public Player(float x, float y, float width, float height){
        super(x, y, width, height, Color.BLACK);

        //TODO: Replace ShapeRendering with Animation
        forceField = ForceField.BUBBLE;
    }
}
