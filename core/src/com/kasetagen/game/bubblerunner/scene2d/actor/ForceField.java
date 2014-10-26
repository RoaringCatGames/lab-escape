package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.*;
import com.kasetagen.game.bubblerunner.util.ForceFieldColorUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 6/1/14
 * Time: 7:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForceField extends com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericActor {

    public ForceFieldType forceFieldType;
    public float targetRadius;
    public float radius;

    public ForceField(float x, float y, float radius, ForceFieldType ff){
        super(x, y, radius*2, radius*2, null, ForceFieldColorUtil.getColor(ff));

        forceFieldType = ff;
        targetRadius = radius;
        this.radius = radius;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        //Manually "Tween" our position
        if(targetRadius > radius){
            radius++;
            setX(getX() - 1);
            setY(getY() -1);
            setWidth(radius*2);
            setHeight(radius*2);
        }else if(targetRadius < radius){
            radius--;
            setX(getX() + 1);
            setY(getY() + 1);
            setWidth(radius*2);
            setHeight(radius*2);
        }
    }

    @Override
    public void drawFull(Batch batch, float parentAlpha) {

        batch.end();
        batch.begin();
        Gdx.gl20.glLineWidth(5f);
        debugRenderer.setProjectionMatrix(getStage().getCamera().combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(getColor());
        debugRenderer.circle(getOriginX(), getOriginY(), radius);
        debugRenderer.end();
        batch.end();
        batch.begin();
    }
}
