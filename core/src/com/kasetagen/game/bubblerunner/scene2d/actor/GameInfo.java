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

    private static final int TEXT_PADDING = 20;
    private static final int DEFAULT_MAX_FIELDS = 1;
    private static final String SCORE_PREFIX = "Score: ";
    private static final String FIELD_PREFIX = "Max Fields: ";
    private static final String MISSES_PREFIX = "Misses: ";
    private Label scoreLabel;
    private Label missesLabel;
    private Label maxFieldsLabel;
    private Label resourceLabel;

    public int score = 0;
    public int misses = 0;
    public int maxFields = DEFAULT_MAX_FIELDS;

    private ControlGroup controls;

    public GameInfo(float x, float y, float width, float height, BitmapFont font, ControlGroup player) {
        super(x, y, width, height, null, Color.WHITE);

        controls = player;

        LabelStyle style = new LabelStyle(font, getColor());

        scoreLabel = new Label(SCORE_PREFIX + score, style);
        scoreLabel.setPosition(0, 0);
        addActor(scoreLabel);

        missesLabel = new Label(MISSES_PREFIX + misses, style);
        missesLabel.setPosition(scoreLabel.getWidth() + TEXT_PADDING, 0);
        addActor(missesLabel);

        maxFieldsLabel = new Label(FIELD_PREFIX + maxFields, style);
        maxFieldsLabel.setPosition(scoreLabel.getWidth() + TEXT_PADDING +
                                   maxFieldsLabel.getWidth() + TEXT_PADDING, 0);
        addActor(maxFieldsLabel);

        resourceLabel = new Label(getResourceLevelString(), style);
        resourceLabel.setPosition(scoreLabel.getWidth() + TEXT_PADDING +
                                  maxFieldsLabel.getWidth() + TEXT_PADDING +
                                  missesLabel.getWidth() + TEXT_PADDING, 0);
        addActor(resourceLabel);
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        scoreLabel.setText(SCORE_PREFIX + score);
        missesLabel.setText(MISSES_PREFIX + misses);
        maxFieldsLabel.setText(FIELD_PREFIX + maxFields);
        resourceLabel.setText(getResourceLevelString());
    }

    @Override
    protected void drawFull(Batch batch, float parentAlpha) {
        super.drawFull(batch, parentAlpha);

    }

    public String getResourceLevelString(){
        return  "Lightning: " + controls.getResourceLevel(ForceFieldType.LIGHTNING) +
                " Laser: " + controls.getResourceLevel(ForceFieldType.LASER) +
                " Plasma: " + controls.getResourceLevel(ForceFieldType.PLASMA);
    }

    public void reset(){
        score = 0;
        misses = 0;
        maxFields = DEFAULT_MAX_FIELDS;
    }
}
