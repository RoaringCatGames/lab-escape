package com.kasetagen.game.bubblerunner.data;

import com.badlogic.gdx.utils.Array;
import com.kasetagen.game.bubblerunner.scene2d.actor.ForceFieldType;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 7/5/14
 * Time: 12:54 PM
 */
public class WallPattern {

    public int wallCount;
    public Array<ForceFieldType> forceFields;
    public float wallPadding;

    public WallPattern(float padding){
        wallPadding = padding;
        forceFields = new Array<ForceFieldType>();
        wallCount = 0;
    }

    public void addWall(ForceFieldType fft){
        wallCount++;
        forceFields.add(fft);
    }
}
