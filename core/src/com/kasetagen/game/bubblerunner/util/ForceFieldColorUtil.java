package com.kasetagen.game.bubblerunner.util;

import com.badlogic.gdx.graphics.Color;
import com.kasetagen.game.bubblerunner.scene2d.actor.ForceFieldType;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 11:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForceFieldColorUtil {

    public static Color getColor(ForceFieldType ff){
        Color result;
        switch(ff){
            case LIGHTNING:
                result = Color.BLUE;
                break;
            case PLASMA:
                result = Color.GREEN;
                break;
            case LASER:
                result =Color.RED;
                break;
            default:
                result = Color.ORANGE;
                break;

        }
        return result;
    }
}
