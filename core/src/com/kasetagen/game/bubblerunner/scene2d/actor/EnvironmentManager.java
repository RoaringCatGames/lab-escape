package com.kasetagen.game.bubblerunner.scene2d.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kasetagen.game.bubblerunner.scene2d.BubbleRunnerStage;

/*
 * Manages environment objects and allows creation of new grouped objects. Can set group vel
 */

public class EnvironmentManager {
    private static ObjectMap<String, EnvironmentGroup> envGroupMap;
    private static Stage stage;
    
    public static void initialize(Stage stage){
    	EnvironmentManager.stage = stage;
    	envGroupMap = new ObjectMap<String, EnvironmentGroup>();
    }
    
    private static void addGroupIfMissing(String key){
    	if(!envGroupMap.containsKey(key)){
    		envGroupMap.put(key, new EnvironmentGroup(0, 0, stage.getWidth(), stage.getHeight(), Color.BLACK));
    	}
    }
    
    public static void addActor(EnvironmentObj env, boolean isAscendingOrder, String key){
    	EnvironmentGroup envGroup = null;
    	
    	// Add envGroup if missing;
    	addGroupIfMissing(key);
    	
    	envGroup = envGroupMap.get(key);
    	
    	if(isAscendingOrder || envGroup.getChildren().size == 0)
    		envGroup.addActor(env);
    	else{
    		envGroup.addActor(env);
    		
        	for(int i = 0, size = envGroup.getChildren().size; i < size; i++){
        		envGroup.getChildren().get(i).setZIndex((size - 1) - i);
        	}
    	}    		
    	//env.setZIndex(0);
    }
    
    public static void setEnvironmentGroupVelocity(float vel, String key){
    	addGroupIfMissing(key);
    	
    	EnvironmentGroup envGroup = envGroupMap.get(key);
    	
    	for(Actor env:envGroup.getChildren()){
    		((EnvironmentObj)env).setXVelocity(vel);
    	}
    }
    
    public static void proccessEnvironmentGroupMovement(float delta, String key){
    	addGroupIfMissing(key);
    	
    	EnvironmentGroup envGroup = null;
    	
    	if(envGroupMap.containsKey(key)){
    		envGroup = envGroupMap.get(key);
    	
	    	for(Actor env:envGroup.getChildren()){
	    		((EnvironmentObj)env).setX(env.getX() + (((EnvironmentObj)env).velocity.x * delta));
	    	}
    	}
    }
    
    public static EnvironmentObj getLastEnvironment(String key){
    	addGroupIfMissing(key);
    	
    	EnvironmentGroup envGroup = null;
    	EnvironmentObj prevEnv = null;
    	
    	if(envGroupMap.containsKey(key)){
    		envGroup = envGroupMap.get(key);
	
	    	if(envGroup.getChildren().size != 0)
	    		prevEnv = (EnvironmentObj) envGroup.getChildren().get(envGroup.getChildren().size - 1);
    	}
	    	
    	return prevEnv;
    }
    
    public static Array<EnvironmentGroup> getEnvironmentGroups(){    	
    	Array<EnvironmentGroup> groups = new Array<EnvironmentGroup>();
    	
    	for(EnvironmentGroup envGroup:envGroupMap.values()){
    		groups.add(envGroup);
    	}
    	
    	return groups;
    }
    
    public static EnvironmentGroup getEnvironmentGroup(String key){ 
    	addGroupIfMissing(key);
    	
    	return envGroupMap.get(key);
    }
    
    public static ObjectMap<String, EnvironmentGroup> getMap(){
    	return envGroupMap;
    }
    
    public static void setEnvironmentGroupZIndex(int index, String key){
    	addGroupIfMissing(key);
    	
    	EnvironmentGroup envGroup = null;
    	
    	if(envGroupMap.containsKey(key)){
    		envGroup = envGroupMap.get(key);
    	
	    	for(Actor env:envGroup.getChildren()){
	    		((EnvironmentGroup)env).setZIndex(index);
	    	}
    	}
    }
    
    public static void removeOutOfBoundsEnvironments(){
        for(EnvironmentGroup envGroup:envGroupMap.values()){
        	for(Actor env:envGroup.getChildren()){
	        	if(env.getOriginX()+env.getWidth()/2 < 0){
	        		envGroup.removeActor(env);
	        	}
        	}
        }
    }
}
