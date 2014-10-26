package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.kasetagen.game.bubblerunner.scene2d.actor.Entity;
import com.kasetagen.game.bubblerunner.util.ViewportUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 8/4/14
 * Time: 10:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseStage extends Stage {
	protected PerspectiveCamera cam;
	protected CameraInputController camController;
	protected Array<Entity> entityArray = new Array<Entity>();
	protected Array<Decal> decalArray = new Array<Decal>();
	protected DecalBatch decalBatch;
	
    public BaseStage(){
        super(new StretchViewport(ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT));
    }

    public PerspectiveCamera get3dCamera(){
    	return cam;
    }
    
    public CameraInputController getCameraInputController(){
    	return camController;
    }
    
    public void add3dActor(Entity entity){
    	entityArray.add(entity);
    }
    
    public void add3dActor(Decal decal){
    	decalArray.add(decal);
    }
    
    public DecalBatch getDecalBatch(){
    	return decalBatch;
    }

}
