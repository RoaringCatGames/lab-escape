package com.kasetagen.game.bubblerunner.data;

import com.kasetagen.game.bubblerunner.scene2d.actor.ForceFieldType;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 4/26/15
 * Time: 3:31 PM
 */
public class ResourceLimitation {

    private int OVERHEAT_POINT = 40;
    private int heatScore = 0;

    public ResourceLimitation(int maximum){
        heatScore = 0;
        OVERHEAT_POINT = maximum;
    }


    public int getResourceLevel(){
        return heatScore;
    }

    public int getHeatMax(){
        return OVERHEAT_POINT;
    }

    public void incrementHeat(int increment){
        heatScore += increment;
        if(heatScore < 0){
            heatScore = 0;
        }
    }

    public void restoreAllResourceLevels(){
        heatScore = 0;
    }
}
