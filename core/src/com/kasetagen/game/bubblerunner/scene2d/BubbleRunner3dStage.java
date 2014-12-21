package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.kasetagen.engine.IGameProcessor;
//import com.kasetagen.game.bubblerunner.delegate.IGameProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:37 PM
 * This is our main game processing class.
 */
public class BubbleRunner3dStage extends BaseStage {
    private static final float HUD_HEIGHT = 40f;
    private static final float FLOOR_HEIGHT = 160f;

    private static final int SECONDS_BETWEEN_DIFF_SHIFT = 30;
    private static final long BASE_TIME_BETWEEN_WALLS = 4000L;
    private static final int SECONDS_BETWEEN_ADJUSTS = 5;
    private static final float TIME_DECREASE = 100f;
    private static final float MIN_TIME_BETWEEN_WALLS = 300f;
    
    //Delegates
	private IGameProcessor gameProcessor;
	private AssetManager assetManager;
    private Batch batch;
    
    //Camera
    public PerspectiveCamera cam;
    public Model model;
    public ModelInstance instance;
    
    public BubbleRunner3dStage(IGameProcessor gameProcessor){
        super(gameProcessor);
        
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(5f, 5f, 5f,
            new Material(ColorAttribute.createDiffuse(Color.GREEN)),
            Usage.Position | Usage.Normal);
        instance = new ModelInstance(model);
    }
    
    public void resume(){
        Gdx.app.log("RESUMING", "APPLICATION RESUMING");
    }
    
    public void toggleListener(){
    	
    }

}
