package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericActor;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/3/15
 * Time: 1:32 PM
 */
public class Indicator extends GenericActor {

    private static final float ALPHA_ADJUST = 0.5f;

    private boolean isCovered;
    public Indicator(float x, float y, float width, float height, TextureRegion textureRegion) {
        super(x, y, width, height, textureRegion, Color.DARK_GRAY);
        isCovered = false;
    }

    @Override
    protected void drawBefore(Batch batch, float parentAlpha) {
        super.drawBefore(batch, parentAlpha);
        if(isCovered){
            Color c = batch.getColor();
            c.a = c.a*ALPHA_ADJUST;
        }
    }

    @Override
    protected void drawAfter(Batch batch, float parentAlpha) {
        super.drawAfter(batch, parentAlpha);
        if(isCovered){
            Color c = batch.getColor();
            c.a = c.a/ALPHA_ADJUST;
        }
    }
}
