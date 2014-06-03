package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.kasetagen.game.bubblerunner.util.ForceFieldColorUtil;


/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class Player extends GenericActor {

    //set them in reverse order so they match with the order of the
    //  forcefields being added.
    private float[] forceFieldColliders = new float[] { 200f, 180f, 160f };
    public ForceFieldType forceFieldType;


    public int maxFields = 3;
    private Array<ForceFieldType> fields;

    public Player(float x, float y, float width, float height){
        super(x, y, width, height, Color.BLACK);



        //TODO: Replace ShapeRendering with Animation
        forceFieldType = ForceFieldType.BUBBLE;
        fields = new Array<ForceFieldType>();
    }

    public void addField(ForceFieldType ff){
        if(fields.size == maxFields){
            fields.removeIndex(0);
        }
        fields.add(ff);
    }

    public void addField(ForceFieldType ff, int index){

        fields.insert(index, ff);
    }

    public void removeField(ForceFieldType ff){
        int removeIndex = -1;
        for(int i=0;i<fields.size;i++){
            if(ff.equals(fields.get(i))){
                removeIndex = i;
                break;
            }
        }

        if(removeIndex >= 0){
            fields.removeIndex(removeIndex);
        }
    }

    public void clearFields(){
        fields.clear();
    }

    public int getFieldsSize(){
        return fields.size;
    }

    public ForceFieldType getOuterForceField(){
        ForceFieldType ff = null;
        if(fields.size > 0){
            ff = fields.get(0);
        }

        return ff;
    }

    public float getOuterForceFieldPosition(){
        float collidingPosition = getOriginX();
        if(fields.size > 0){
            collidingPosition += forceFieldColliders[3-fields.size];
        }

        return collidingPosition;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.end();
        batch.begin();
        Gdx.gl20.glLineWidth(5f);
        debugRenderer.setProjectionMatrix(getStage().getCamera().combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        //float radius = 160f;
        for(int i = fields.size -1; i>=0;i--){
            debugRenderer.setColor(getColor());
            debugRenderer.setColor(ForceFieldColorUtil.getColor(fields.get(i)));
            debugRenderer.circle(getOriginX(), getOriginY(), forceFieldColliders[i]);
            debugRenderer.setColor(Color.WHITE);
            debugRenderer.rect(getOuterForceFieldPosition(), getOuterForceFieldPosition() + 5f, 5f, 5f);
            //radius += 20f;
        }
        debugRenderer.end();
        batch.end();
        batch.begin();

    }
}
