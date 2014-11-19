package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.graphics.Camera;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 11/16/14
 * Time: 1:01 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ICameraModifier {

    public void modify(Camera camera, float delta);
    public boolean isComplete();

}
