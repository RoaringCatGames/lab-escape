package com.kasetagen.game.bubblerunner.util;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 10/21/14
 * Time: 8:56 PM
 */
public class AnimationUtil {

    public static final float RUNNER_CYCLE_RATE = 1f/8f;
    public static final float RUNNER_FIRE_CYCLE_RATE = 1f/6f;
    public static final float RUNNER_ELECTRO_CYCLE_RATE = 1f/10f;
    public static final float RUNNER_WALL_CYCLE_RATE = 1f/8f;

    public static final float TITLE_CYCLE_RATE = 1f/6f;

    public static final float SHIELD_CYCLE_RATE = 1f/8f;
    public static final String CHARACTER_1 = "Edison";
    public static final String CHARACTER_2 = "Edyn";


    public static String getPlayerAnimationName(String characterSelected) {
        return characterSelected.equals(AnimationUtil.CHARACTER_2) ? AtlasUtil.ANI_VEDA_RUN : AtlasUtil.ANI_FRED_RUN;
    }

    public static String getPlayerShieldingAnimationName(String characterSelected){
        return characterSelected.equals(AnimationUtil.CHARACTER_2) ? AtlasUtil.ANI_VEDA_PUNCH : AtlasUtil.ANI_FRED_PUNCH;
    }
    public static String getPlayerElectroAnimationName(String characterSelected){
        return characterSelected.equals(AnimationUtil.CHARACTER_2) ? AtlasUtil.ANI_VEDA_SHOCK : AtlasUtil.ANI_FRED_SHOCK;
    }
    public static String getPlayerWallAnimationName(String characterSelected){
        return characterSelected.equals(AnimationUtil.CHARACTER_2) ? AtlasUtil.ANI_VEDA_WALL : AtlasUtil.ANI_FRED_WALL;
    }
    public static String getPlayerFireAnimationName(String characterSelected){
        return characterSelected.equals(AnimationUtil.CHARACTER_2) ? AtlasUtil.ANI_VEDA_FIRE : AtlasUtil.ANI_FRED_FIRE;
    }
}
