package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericGroup;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 10/30/14
 * Time: 9:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShieldGroup extends GenericGroup {

    private static final float SHIELD_WIDTH = 100f;
    private static final float SHIELD_HEIGHT = 360f;

    private static final float FIELD_ADJUST_X = SHIELD_WIDTH;
    private static final float FIELD_ADJUST_Y = 75f;

    private Array<ForceField> fields;
    public int maxFields = 1;
    public int resourceUsage = 1;
    private ObjectMap<ForceFieldType, Animation> shieldAnimations;

    public ShieldGroup(float x, float y, float width, float height) {
        super(x, y, width, height, null, Color.BLACK);
        fields = new Array<ForceField>();
        shieldAnimations = new ObjectMap<ForceFieldType, Animation>();
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

        float radius = getWidth();
        float x = getWidth() - (SHIELD_WIDTH/4);
        float y = FIELD_ADJUST_Y;
        ForceField field = new ForceField(x, y, SHIELD_WIDTH, SHIELD_HEIGHT, shieldAnimations.get(ff), ff);

        for(int i=0;i<fields.size;i++){
            fields.get(i).targetX += FIELD_ADJUST_X;
        }
//        for(int i=fields.size-1;i>=0;i--){
//            fields.get(i).targetX = fields.get(i).targetX + (FIELD_ADJUST_X);
//        }

        this.addActor(field);
        fields.add(field);
    }

    public void removeField(ForceField ff){
        this.removeActor(ff);
        fields.removeValue(ff, true);

        for(int i=0;i<fields.size;i++){
            fields.get(i).targetX -= FIELD_ADJUST_X*(fields.size-i-1);//= getWidth()-(SHIELD_WIDTH/4) + (FIELD_ADJUST_X * (fields.size-i-1));
        }
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
