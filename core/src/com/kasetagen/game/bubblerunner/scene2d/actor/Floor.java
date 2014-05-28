package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 10:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Floor extends GenericActor {

    public Floor(float x, float y, float width, float height){
        super(x, y, width, height, Color.DARK_GRAY);
    }
}
