package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 6/8/14
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class GameInfo extends GenericGroup {

    private static final int TEXT_PADDING = 60;
    private static final String SCORE_PREFIX = "Score: ";
    private static final String FIELD_PREFIX = "Max Fields: ";
    private Label scoreLabel;
    private Label maxFieldsLabel;
    public int score = 0;
    public int maxFields = 3;

    public GameInfo(float x, float y, float width, float height, BitmapFont font) {
        super(x, y, width, height, null, Color.WHITE);

        LabelStyle style = new LabelStyle(font, getColor());

        scoreLabel = new Label(SCORE_PREFIX + score, style);
        scoreLabel.setPosition(0, 0);
        addActor(scoreLabel);


        maxFieldsLabel = new Label(FIELD_PREFIX + maxFields, style);
        maxFieldsLabel.setPosition(scoreLabel.getWidth() + TEXT_PADDING, 0);
        addActor(maxFieldsLabel);
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        scoreLabel.setText(SCORE_PREFIX + score);
        maxFieldsLabel.setText(FIELD_PREFIX + maxFields);
    }

    @Override
    protected void drawFull(Batch batch, float parentAlpha) {
        super.drawFull(batch, parentAlpha);

    }
}
