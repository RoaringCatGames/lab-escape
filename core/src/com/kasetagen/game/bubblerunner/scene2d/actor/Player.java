package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class Player extends GenericGroup {

    private static final float FIELD_ADJUST = 20f;

    //set them in reverse order so they match with the order of the
    //  forcefields being added.
    private float[] forceFieldColliders = new float[] { 200f, 180f, 160f };
    public ForceFieldType forceFieldType;


    public int maxFields = 3;
    //private Array<ForceFieldType> fields;
    private Array<ForceField> fields;

    public Player(float x, float y, float width, float height, TextureRegion textureRegion){
        super(x, y, width, height, textureRegion, Color.BLACK);


        //TODO: Replace ShapeRendering with Animation
        forceFieldType = ForceFieldType.BUBBLE;
        //fields = new Array<ForceFieldType>();
        fields = new Array<ForceField>();
    }

    public void addField(ForceFieldType ff){
        if(fields.size == maxFields){
            //Remove the forcefield
            ForceField f = fields.get(0);
            this.removeActor(f);
            fields.removeIndex(0);
        }

        float radius = 160f;
        float x = getX()-getWidth()/2;
        ForceField field = new ForceField(x, getY(), radius, ff);

        this.addActor(field);
        fields.add(field);

        for(int i=fields.size-1;i>=0;i--){
            fields.get(i).targetRadius = fields.get(i).targetRadius + (FIELD_ADJUST);
        }
    }

    public void addField(ForceFieldType ff, int index){

        float radius = getWidth() * (index + 1);
        ForceField field = new ForceField(getOriginX(), getOriginY(), radius, ff);

        //Remove the overriding forcefield
        if(fields.size > index){
            ForceField f = fields.get(index);
            if(f != null){
                this.removeActor(f);
            }
        }
        this.addActor(field);
        fields.insert(index, field);
    }

    public void removeField(ForceFieldType ff){
        int removeIndex = -1;
        for(int i=0;i<fields.size;i++){
            if(ff.equals(fields.get(i).forceFieldType)){
                removeIndex = i;
                break;
            }
        }

        if(removeIndex >= 0){
            ForceField f = fields.get(removeIndex);
            this.removeActor(f);
            fields.removeIndex(removeIndex);
        }
    }

    public void clearFields(){

        for(ForceField f:fields){
            this.removeActor(f);
        }
        fields.clear();
    }

    public int getFieldsSize(){
        return fields.size;
    }

    public ForceFieldType getOuterForceField(){
        ForceFieldType ff = null;
        if(fields.size > 0){
            ff = fields.get(0).forceFieldType;
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

//    @Override
//    public void drawFull(Batch batch, float parentAlpha) {
//        super.draw(batch, parentAlpha);
//
//        batch.end();
//        batch.begin();
//        Gdx.gl20.glLineWidth(5f);
//        debugRenderer.setProjectionMatrix(getStage().getCamera().combined);
//        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
//
//        //float radius = 160f;
//        for(int i = fields.size -1; i>=0;i--){
//            debugRenderer.setColor(getColor());
//            debugRenderer.setColor(ForceFieldColorUtil.getColor(fields.get(i).forceFieldType));
//            debugRenderer.circle(getOriginX(), getOriginY(), forceFieldColliders[i]);
//            debugRenderer.setColor(Color.WHITE);
//            debugRenderer.rect(getOuterForceFieldPosition(), getOuterForceFieldPosition() + 5f, 5f, 5f);
//            //radius += 20f;
//        }
//        debugRenderer.end();
//        batch.end();
//        batch.begin();
//
 //   }
}
