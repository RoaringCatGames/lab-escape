package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 6/9/14
 * Time: 9:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Overlay extends GenericGroup {
    private String mainText;
    private String subText;

    private Label mainLabel;
    private Label subLabel;

    public Overlay(float x, float y, float width, float height, Color bgColor,
                   Color textColor, BitmapFont mainFont, BitmapFont subFont, String mainText, String subText) {
        super(x, y, width, height, bgColor);

        this.mainText = mainText;
        this.subText = subText;

        Label.LabelStyle mainStyle = new Label.LabelStyle(mainFont, textColor);
        mainLabel = new Label(mainText, mainStyle);

        Label.LabelStyle subStyle = new Label.LabelStyle(subFont, textColor);
        subLabel = new Label(subText, subStyle);

        float mainX = getWidth()/2 - mainLabel.getWidth()/2;
        float mainY = ((getHeight()/4)*3) - mainLabel.getHeight()/2;
        mainLabel.setPosition(mainX, mainY);
        addActor(mainLabel);

        float subX = getWidth()/2 - subLabel.getWidth()/2;
        float subY = (getHeight()/3) - subLabel.getHeight()/2;
        subLabel.setPosition(subX, subY);
        addActor(subLabel);
    }

    public void setMainText(String text){
        mainText = text;
    }

    public void setSubText(String text){
        subText = text;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        mainLabel.setText(mainText);
        subLabel.setText(subText);
    }
}
