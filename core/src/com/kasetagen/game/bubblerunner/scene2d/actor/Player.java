package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
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

    public ForceFieldType forceFieldType;


    public int maxFields = 3;
    private Array<ForceField> fields;


    public Player(float x, float y, float width, float height, TextureRegion textureRegion){
        super(x, y, width, height, textureRegion, Color.BLACK);

        //TODO: Replace ShapeRendering with Animation
        forceFieldType = ForceFieldType.BUBBLE;
        fields = new Array<ForceField>();
    }

    public void addField(ForceFieldType ff){
        if(fields.size == maxFields){
            //Remove the forcefield
            ForceField f = fields.get(0);
            this.removeActor(f);
            fields.removeIndex(0);
        }

        float radius = getWidth();
        float x = getX()-getWidth()/2;
        ForceField field = new ForceField(x, getY(), radius, ff);

        this.addActor(field);
        fields.add(field);

        for(int i=fields.size-1;i>=0;i--){
            fields.get(i).targetRadius = fields.get(i).targetRadius + (FIELD_ADJUST);
        }
    }

    public void addField(ForceFieldType ff, int index){

        float adjust = fields.size > 0 ? FIELD_ADJUST *fields.size - 1 : 0;
        float radius = getWidth() + adjust;
        float x = getX() - (getWidth()/2) - adjust;
        float y = getY() - adjust;
        ForceField field = new ForceField(x, y, radius, ff);
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

        //When we remove a forcefield, we need to readjust all of the remaining
        //  fields into the proper positions.
        for(int i=0;i<fields.size;i++){
            fields.get(i).targetRadius = getWidth() + (FIELD_ADJUST * (fields.size-i-1));
        }
    }

    public void clearFields(){

        for(ForceField f:fields){
            this.removeActor(f);
        }
        fields.clear();
    }

    public ForceFieldType getOuterForceField(){
        ForceFieldType ff = null;
        if(fields.size > 0){
            ff = fields.get(0).forceFieldType;
        }

        return ff;
    }

    public Rectangle getOuterForceFieldCollider(){
        Rectangle collider = null;
        if(fields.size > 0){
            collider = fields.get(0).collider;
        }

        return collider;
    }

    @Override
    public void drawFull(Batch batch, float parentAlpha) {
        //This is where we'll draw our animation of the player running
    }
}
