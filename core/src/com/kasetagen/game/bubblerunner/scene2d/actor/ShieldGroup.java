package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericGroup;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 10/30/14
 * Time: 9:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShieldGroup extends GenericGroup {

    private static final float FIELD_ADJUST = 20f;

    private Array<ForceField> fields;
    public int maxFields = 1;
    public int resourceUsage = 1;

    public ShieldGroup(float x, float y, float width, float height) {
        super(x, y, width, height, null, Color.YELLOW);
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
        float y = getY();
        ForceField field = new ForceField(x, y, radius, ff);

        for(int i=fields.size-1;i>=0;i--){
            fields.get(i).targetRadius = fields.get(i).targetRadius + (FIELD_ADJUST);
        }

        this.addActor(field);
        fields.add(field);
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

    public void removeField(ForceField ff){
        this.removeActor(ff);
        fields.removeValue(ff, true);

        for(int i=0;i<fields.size;i++){
            fields.get(i).targetRadius = getWidth() + (FIELD_ADJUST * (fields.size-i-1));
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
            fields.get(i).targetRadius = getWidth() + (FIELD_ADJUST * (fields.size-i-1));
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