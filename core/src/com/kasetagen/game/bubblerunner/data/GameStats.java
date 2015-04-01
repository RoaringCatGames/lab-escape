package com.kasetagen.game.bubblerunner.data;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:47 PM
 */
public class GameStats {
    //TODO: Implement GameStats
    public static final String HIGH_SCORE_KEY = "HIGH_SCORE";
    public static final String MOST_MISSES_KEY = "MOST_MISSES";
    public static final String HIGH_COMBO_KEY = "HIGHEST_COMBO";

    public int score = 0;
    public int maxFields = 3;


    public GameStats(){
        score = 0;
        maxFields = 3;
    }

    public GameStats(int score, int fields){
        this.score = score;
        this.maxFields = fields;
    }

    @Override
    public String toString() {
        return "SCORE:" + score + "|MAXFIELDS:" + maxFields;
    }


    public static GameStats fromString(String serializedInfo){
        String[] kvps = serializedInfo.split("|");
        int parseScore = 0,
            parsedFieldCount = 0;

        for(String kvp:kvps){
            String[] prop = kvp.split(":");
            if("SCORE".equalsIgnoreCase(prop[0])){
                parseScore = Integer.parseInt(prop[1]);
            }else if("MAXFIELDS".equalsIgnoreCase(prop[0])){
                parsedFieldCount = Integer.parseInt(prop[1]);
            }
        }
        return new GameStats(parseScore, parsedFieldCount);
    }
}
