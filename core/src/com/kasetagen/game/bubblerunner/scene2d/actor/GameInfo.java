package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericGroup;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 6/8/14
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class GameInfo extends GenericGroup {

    public static final int DEFAULT_MAX_FIELDS = 2;

    private static final int TEXT_PADDING = 60;
    private static final String SCORE_PREFIX = "Score: ";
    private static final String FIELD_PREFIX = "Max Fields: ";
    private static final String MISSES_PREFIX = "Misses: ";
    private Label scoreLabel;
    private Label missesLabel;
    private Label maxFieldsLabel;

    public int score = 0;
    public int misses = 0;
    public int maxFields = DEFAULT_MAX_FIELDS;

    //private ControlGroup controls;

    public GameInfo(float x, float y, float width, float height, BitmapFont font, ControlGroup player) {
        super(x, y, width, height, null, Color.DARK_GRAY);

        //controls = player;

        LabelStyle style = new LabelStyle(font, Color.BLACK);//getColor());
        style.font.setScale(1f);
        scoreLabel = new Label(SCORE_PREFIX + score, style);
        scoreLabel.setPosition(0, 0);
        addActor(scoreLabel);

        missesLabel = new Label(MISSES_PREFIX + misses, style);
        missesLabel.setPosition(scoreLabel.getWidth() + TEXT_PADDING, 0);
        addActor(missesLabel);

        maxFieldsLabel = new Label(FIELD_PREFIX + maxFields, style);
        maxFieldsLabel.setPosition(scoreLabel.getWidth() + TEXT_PADDING +
                                   missesLabel.getWidth() + TEXT_PADDING, 0);
        addActor(maxFieldsLabel);
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        scoreLabel.setText(SCORE_PREFIX + score);
        missesLabel.setText(MISSES_PREFIX + misses);
        maxFieldsLabel.setText(FIELD_PREFIX + maxFields);
    }

    @Override
    protected void drawFull(Batch batch, float parentAlpha) {
        super.drawFull(batch, parentAlpha);

    }

    public void reset(){
        score = 0;
        misses = 0;
        maxFields = DEFAULT_MAX_FIELDS;
    }
}
