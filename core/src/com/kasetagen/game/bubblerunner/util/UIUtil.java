package com.kasetagen.game.bubblerunner.util;

/**
 * Created by barry on 5/6/15.
 * Utility to hold basic UI operations
 */
public class UIUtil {

    public static String convertIntToDigitsString(int maxDigits, int score){
        StringBuilder sb = new StringBuilder("");
        int currentDigits = String.valueOf(score).length();
        while(currentDigits < maxDigits){
            currentDigits++;
            sb.append("0");
        }
        sb.append(score);
        return sb.toString();
    }
}
