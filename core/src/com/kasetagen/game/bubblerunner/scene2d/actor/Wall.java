package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class Wall extends GenericActor {

    public Wall(float x, float y, float width, float height){
        super(x, y, width, height, Color.GREEN);
    }
}
