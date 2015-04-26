package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.AnimatedActor;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 7/20/14
 * Time: 3:58 PM
 */
public class ForceFieldImageButton extends AnimatedActor {

    private float transparencyAdjustment = 0.5f;
    public ForceFieldType forceFieldType;

    public ForceFieldImageButton(float x, float y, float width, float height, Animation animation, ForceFieldType fft) {
        super(x, y, width, height, animation, 0f);
        this.forceFieldType = fft;
    }

    private float getTransparencyAdjustment(){
        return "PRESSED".equals(getCurrentState()) ? 1f : transparencyAdjustment;
    }

    @Override
    protected void drawBefore(Batch batch, float parentAlpha) {
        super.drawBefore(batch, parentAlpha);
        Color c = new Color(batch.getColor());
        c.a *= getTransparencyAdjustment();
        batch.setColor(c);
    }

    @Override
    protected void drawAfter(Batch batch, float parentAlpha) {
        super.drawAfter(batch, parentAlpha);
        Color c = new Color(batch.getColor());
        c.a /= getTransparencyAdjustment();
        batch.setColor(c);

    }
}
