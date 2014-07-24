package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 6/9/14
 * Time: 9:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Overlay extends GenericGroup {

    private static final float VERTICAL_PADDING = 30f;

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
        mainLabel.setAlignment(Align.center);

        Label.LabelStyle subStyle = new Label.LabelStyle(subFont, textColor);
        subLabel = new Label(subText, subStyle);
        subLabel.setAlignment(Align.center);

        float mainX = getWidth()/2 - mainLabel.getWidth()/2;
        float mainY = ((getHeight()/4)*3) - mainLabel.getHeight()/2;
        mainLabel.setPosition(mainX, mainY);
        addActor(mainLabel);

        float subX = getWidth()/2 - subLabel.getWidth()/2;
        float subY = mainLabel.getY() - (mainLabel.getHeight()/2) - VERTICAL_PADDING;
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

    @Override
    protected void drawBefore(Batch batch, float parentAlpha) {
        batch.end();
        batch.begin();
        Gdx.gl20.glLineWidth(1f);
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //Set the projection matrix, and line shape
        debugRenderer.setProjectionMatrix(getStage().getCamera().combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //Draw the red origin marker
        debugRenderer.setColor(getColor());
        Color c = Color.WHITE;//getColor();
        debugRenderer.setColor(c.r, c.g, c.b, 0.1f);
        debugRenderer.rect(getX(), getY(), getWidth(), getHeight(), getOriginX(), getOriginY(), getRotation());
        //End our shapeRenderer, flush the batch, and re-open it for future use as it was open
        // coming in.
        debugRenderer.end();
        batch.end();
        batch.begin();

        super.drawBefore(batch, parentAlpha);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
