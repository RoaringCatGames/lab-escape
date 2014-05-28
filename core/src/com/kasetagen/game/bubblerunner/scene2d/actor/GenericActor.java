package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 10:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenericActor extends Actor {
    protected ShapeRenderer shaper;

    public Rectangle collider;

    public GenericActor(float x, float y, float width, float height, Color color){
        shaper = new ShapeRenderer();
        setPosition(x, y);
        setBounds(x, y, width, height);
        setWidth(width);
        setHeight(height);
        setOrigin(x + width/2, y + height/2);
        setColor(color);
        collider = new Rectangle(x, y, width, height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.end();
        batch.begin();

        shaper.setProjectionMatrix(getStage().getCamera().combined);
        shaper.begin(ShapeRenderer.ShapeType.Filled);
        shaper.setColor(getColor());
        shaper.rect(getX(), getY(), getWidth(), getHeight(), getOriginX(), getOriginY(), getRotation());
        shaper.setColor(Color.RED);
        shaper.circle(getOriginX(), getOriginY(), 3f);
        shaper.end();
        batch.end();
        batch.begin();
    }
}
