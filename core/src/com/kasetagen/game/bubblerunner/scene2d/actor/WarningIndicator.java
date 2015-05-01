package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericActor;
import com.kasetagen.game.bubblerunner.data.WallPattern;
import com.kasetagen.game.bubblerunner.util.AtlasUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 7/27/14
 * Time: 11:11 AM
 */
public class WarningIndicator extends GenericActor {

    private static final float MAX_ALPHA = 1f;
    private static final float MIN_ALPHA = 0.05f;
    private static float SHIFT_PER_SECOND = 1f;

    private WallPattern pattern;
    private float alpha = MAX_ALPHA;
    private boolean isDecreasing = true;
    private Array<TextureRegion> indicatorRegions;
    private float timeAlive = 0f;

    public float lifetime = 0f;

    public WarningIndicator(float x, float y, float width, float height, WallPattern pattern, Color color, TextureAtlas spriteAtlas, float lifetimeSeconds){

        super(x, y, width, height, null, color);
        this.pattern = pattern;

        this.textureRegion = spriteAtlas.findRegion(AtlasUtil.SPRITE_WARNING_INDICATOR);//new TextureRegion(this.texture, texture.getWidth(), 180);

        indicatorRegions = new Array<TextureRegion>();
        for(ForceFieldType fft:pattern.forceFields){
            TextureRegion r;
            switch (fft){
                case LASER:
                    r = spriteAtlas.findRegion(AtlasUtil.SPRITE_RED_INDICATOR);
                    break;
                case PLASMA:
                    r = spriteAtlas.findRegion(AtlasUtil.SPRITE_GREEN_INDICATOR);
                    break;
                case LIGHTNING:
                    r = spriteAtlas.findRegion(AtlasUtil.SPRITE_BLUE_INDICATOR);
                    break;
                default:
                    r = spriteAtlas.findRegion(AtlasUtil.SPRITE_RED_INDICATOR);
                    break;
            }

            indicatorRegions.add(r);

            SHIFT_PER_SECOND = (MAX_ALPHA-MIN_ALPHA)/lifetimeSeconds;
            //We want the lifetime to be one fade out length.
            lifetime = lifetimeSeconds;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        timeAlive += delta;
        if(isAlive()){
            float changeInAlpha = delta*SHIFT_PER_SECOND;

            if(isDecreasing){
                changeInAlpha *= -1;
            }

            alpha += changeInAlpha;
            alpha = alpha > MAX_ALPHA ? MAX_ALPHA : alpha < MIN_ALPHA ? MIN_ALPHA : alpha;

            if(alpha >= MAX_ALPHA && !isDecreasing){
                isDecreasing = true;
            }else if(alpha <= MIN_ALPHA && isDecreasing){
                isDecreasing = false;
            }
        }
    }

    @Override
    protected void drawBefore(Batch batch, float parentAlpha) {
        if(isAlive()){
            super.drawBefore(batch, parentAlpha);
            Color c = batch.getColor();
            batch.setColor(c.r, c.g, c.b, alpha);
        }
    }

    @Override
    protected void drawFull(Batch batch, float parentAlpha) {
        if(isAlive()){
            super.drawFull(batch, parentAlpha);


            float buffer = indicatorRegions.size <= 3 ? 15f : 5f;

            float indicatorSideSize = indicatorRegions.size <= 4 ? 75f : (getWidth() - (buffer*2f*indicatorRegions.size))/indicatorRegions.size;

            for(int i=0;i<indicatorRegions.size;i++){
                float leftBuffer = buffer;
                float x = getX() + leftBuffer + leftBuffer*2f*i;
                float y = (getY() + getHeight()/2f) - (indicatorSideSize/2f);
                batch.draw(indicatorRegions.get(i),
                           x + (indicatorSideSize*i),
                           y,
                           indicatorSideSize,
                           indicatorSideSize);
            }
        }
    }

    @Override
    protected void drawAfter(Batch batch, float parentAlpha) {
        if(isAlive()){
            super.drawAfter(batch, parentAlpha);
            Color c = batch.getColor();
            batch.setColor(c.r, c.g, c.b, parentAlpha);
        }
    }

    private boolean isAlive(){
        return timeAlive <= lifetime;
    }
}
