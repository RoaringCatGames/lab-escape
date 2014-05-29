package com.kasetagen.game.bubblerunner.util;

import com.badlogic.gdx.graphics.Color;
import com.kasetagen.game.bubblerunner.scene2d.actor.ForceField;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 11:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForceFieldColorUtil {

    public static Color getColor(ForceField ff){
        Color result;
        switch(ff){
            case BUBBLE:
                result = Color.BLUE;
                break;
            case ELECTRIC:
                result = Color.YELLOW;
                break;
            case ION:
                result =Color.GREEN;
                break;
            default:
                result = Color.ORANGE;
                break;

        }
        return result;
    }
}
