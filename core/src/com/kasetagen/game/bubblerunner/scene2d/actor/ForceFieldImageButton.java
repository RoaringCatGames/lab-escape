package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 7/20/14
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForceFieldImageButton extends ImageButton {

    public ForceFieldType forceFieldType;
    public ForceFieldImageButton(ImageButtonStyle style, ForceFieldType fft){
        super(style);
        forceFieldType = fft;
    }

}
