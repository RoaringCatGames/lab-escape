package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericGroup;
import com.kasetagen.engine.gdx.scenes.scene2d.decorators.OffsetColliderDecorator;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 10/30/14
 * Time: 9:12 PM
 */
public class ShieldGroup extends GenericGroup {

    private static final float SHIELD_SIZE = 275f/2f;

    private static final float FIELD_ADJUST_X = SHIELD_SIZE+10f;
    private static final float FIELD_ADJUST_Y = 200f;

    private Array<ForceField> fields;
    public int maxFields = 1;
    public int resourceUsage = 1;
    private ObjectMap<ForceFieldType, Animation> shieldAnimations;

    private OffsetColliderDecorator colliderDecorator;

    public ShieldGroup(float x, float y, float width, float height) {
        super(x, y, width, height, null, Color.BLACK);
        fields = new Array<ForceField>();
        shieldAnimations = new ObjectMap<ForceFieldType, Animation>();
        colliderDecorator = new OffsetColliderDecorator(SHIELD_SIZE/4f, SHIELD_SIZE/4f, SHIELD_SIZE/2f, SHIELD_SIZE/2f);
    }

    public void setShieldAnimation(ForceFieldType fft, Animation ani){
        shieldAnimations.put(fft, ani);
    }

    public void addField(ForceFieldType ff){

        if(fields.size == maxFields){
            //Remove the forcefield
            ForceField f = fields.get(0);
            this.removeActor(f);
            fields.removeIndex(0);
        }


        float x = getWidth() - (SHIELD_SIZE/4);
        if(fields.size > 0){
            x -= (fields.get(fields.size-1).getX()-x);
        }
        float y = FIELD_ADJUST_Y;
        ForceField field = new ForceField(x, y, SHIELD_SIZE, SHIELD_SIZE, shieldAnimations.get(ff), ff);
        field.addDecorator(colliderDecorator);
        for(int i=0;i<fields.size;i++){
            fields.get(i).targetX += FIELD_ADJUST_X;
        }
        this.addActor(field);
        fields.add(field);


    }

    public void removeField(ForceField ff){
        this.removeActor(ff);
        fields.removeValue(ff, true);
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
            fields.get(i).targetX = getWidth() + (FIELD_ADJUST_X * (fields.size-i-1));
        }
    }

    public void clearFields(){

        for(ForceField f:fields){
            this.removeActor(f);
        }
        fields.clear();
    }

    public ForceField getOuterForceField(){
        ForceField ff = null;
        if(fields.size > 0){
            ff = fields.get(0);
        }
        return ff;
    }
}
