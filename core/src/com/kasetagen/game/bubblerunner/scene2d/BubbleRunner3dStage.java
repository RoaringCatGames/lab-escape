package com.kasetagen.game.bubblerunner.scene2d;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kasetagen.engine.gdx.scenes.scene2d.KasetagenStateUtil;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;
import com.kasetagen.game.bubblerunner.data.GameOptions;
import com.kasetagen.game.bubblerunner.data.GameStats;
import com.kasetagen.game.bubblerunner.data.IDataSaver;
import com.kasetagen.game.bubblerunner.data.WallPattern;
import com.kasetagen.game.bubblerunner.delegate.IGameProcessor;
import com.kasetagen.game.bubblerunner.scene2d.actor.ControlGroup;
import com.kasetagen.game.bubblerunner.scene2d.actor.EnvironmentManager;
import com.kasetagen.game.bubblerunner.scene2d.actor.EnvironmentObj;
import com.kasetagen.game.bubblerunner.scene2d.actor.ForceField;
import com.kasetagen.game.bubblerunner.scene2d.actor.ForceFieldType;
import com.kasetagen.game.bubblerunner.scene2d.actor.GameInfo;
import com.kasetagen.game.bubblerunner.scene2d.actor.GenericActor;
import com.kasetagen.game.bubblerunner.scene2d.actor.Overlay;
import com.kasetagen.game.bubblerunner.scene2d.actor.Player;
import com.kasetagen.game.bubblerunner.scene2d.actor.Wall;
import com.kasetagen.game.bubblerunner.scene2d.actor.WarningIndicator;
import com.kasetagen.game.bubblerunner.util.AnimationUtil;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;
import com.kasetagen.game.bubblerunner.util.ViewportUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:37 PM
 * This is our main game processing class.
 */
public class BubbleRunner3dStage extends BaseStage {

    private enum ComboLevels {
        NONE, NOT_BAD, GREAT, AWESOME, AMAZING, BONKERS, RIDICULOUS, ATOMIC
    }

    private static final int[] COMBO_THRESHOLDS = new int[] {10, 20, 30, 50, 70, 100, 150};
//    private static final int[] COMBO_THRESHOLDS = new int[] {5, 10, 12, 15, 18, 20, 21};

    private static final float HUD_HEIGHT = 40f;
    private static final float FLOOR_HEIGHT = 160f;

    private static final int SECONDS_BETWEEN_DIFF_SHIFT = 20;
    private static final long BASE_TIME_BETWEEN_WALLS = 4000L;
    private static final int SECONDS_BETWEEN_ADJUSTS = 5;
    private static final float TIME_DECREASE = 100f;
    private static final float MIN_TIME_BETWEEN_WALLS = 300f;

    //private static final int SECONDS_PER_RESOURCE_REGEN = 2;


    //Order of values:  xPos, yPos, width, height
    private static final float INDICATOR_WIDTH = ViewportUtil.VP_WIDTH/4;
    private static final float INDICATOR_HEIGHT = ViewportUtil.VP_HEIGHT/4;

    private static String characterSelected = "Woman";
    private static float[] playerDimensions = new float[] { 100f, FLOOR_HEIGHT, 360f, 360f };//ViewportUtil.VP_WIDTH/8, (ViewportUtil.VP_HEIGHT/3) }; //old width 160f
    //private static float[] floorDimensions = new float[] { 0f, 0f, ViewportUtil.VP_WIDTH, FLOOR_HEIGHT };
    private static float[] wallDimensions = new float[] {ViewportUtil.VP_WIDTH+FLOOR_HEIGHT,
                                                         FLOOR_HEIGHT, 40f, ViewportUtil.VP_HEIGHT-FLOOR_HEIGHT };
    private static float[] warningIndicatorDimensions = new float[] {ViewportUtil.VP_WIDTH/2 - (INDICATOR_WIDTH/2), ViewportUtil.VP_HEIGHT/2-(INDICATOR_HEIGHT/2),
                                                                     INDICATOR_WIDTH, INDICATOR_HEIGHT};

    private static ForceFieldType[] wallTypes = new ForceFieldType[] { ForceFieldType.LIGHTNING, ForceFieldType.PLASMA, ForceFieldType.LASER};

    //Delegates
	private IGameProcessor gameProcessor;
	private AssetManager assetManager;
    //private Batch batch;

    //State Values
    private int highScore = 0;
    private int mostMisses = 0;
    private boolean isDead = false;

    private int currentCombo = 0;
    private int highestRunCombo = 0;
    private int highestCombo = 0;
    private ComboLevels currentComboLevel = ComboLevels.NONE;
    private Label comboLabel;

    //Obstacle Generation Values
    /**
     * Time Passed is in Seconds
     */
    private float millisecondsPassed = 0f;
    private Array<Wall> wallsToRemove;
    private int lastWallAdjustTime = 0;
    private long nextGeneration = 1000L;
    private long millisBetweenWalls = BASE_TIME_BETWEEN_WALLS;
    private float wallAndFloorVelocity;

    private float secondsSinceResourceRegen = 0f;

    //Input Listeners
    private InputListener createAndLeaveListener;
    private InputListener keysReleasedListener;
    private InputListener currentListener;

    //Actors
    public Player player;
    public Array<Wall> walls;
    public Wall collidedWall = null;
    public GameInfo info;
    public Overlay deathOverlay;
    public ControlGroup controls;

    //Ambiance (Music and Effects
    private WarningIndicator warningIndicator;
    private Music music;
    private Sound zapSound;
    private Sound powerOnSound;
    private Sound explosionSound;
    //TODO: Move into Player?
    private ParticleEffect particleBubble;

    private ObjectMap<ComboLevels, Sound> comboSfx;

    private float bgVolume;
    private float sfxVolume;

    private GenericActor instructions;
    
    private enum EnvironmentType {WALL, FLOOR, PILLAR, PLAYER, BACKFLOOR, OBSTACLES};
    
    //3d
	public Environment environment;
	public ModelBatch modelBatch;
	public Model model;
	public ModelInstance instance;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public boolean loading;
	private Array<ModelInstance> labToRemove = new Array<ModelInstance>();
    
    public BubbleRunner3dStage(IGameProcessor gameProcessor){
        super();
        this.gameProcessor = gameProcessor;
    	assetManager = this.gameProcessor.getAssetManager();
    	//batch = this.getBatch();
    	EnvironmentManager.initialize(this);
    	
    	//3d
    	environment = new Environment();
    	//environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(1.0f, 0.2f, 0.2f, -1f, -0.8f, -0.2f));
    	loading = true;
    	
    	modelBatch = new ModelBatch();
    	cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    	cam.near = 1f;
    	cam.far = 300f;
    	cam.update();
    	camController = new CameraInputController(cam);
    	Gdx.input.setInputProcessor(camController);
    	
        //Initialize Privates
        wallsToRemove = new Array<Wall>();
        //floorsToRemove = new Array<Environment>();

        highScore = gameProcessor.getStoredInt(GameStats.HIGH_SCORE_KEY);
        mostMisses = gameProcessor.getStoredInt(GameStats.MOST_MISSES_KEY);
        highestCombo = gameProcessor.getStoredInt(GameStats.HIGH_COMBO_KEY);
        characterSelected = gameProcessor.getStoredString(GameOptions.CHARACTER_SELECT_KEY, "Woman");

        //SET WALL VELOCITY
        wallAndFloorVelocity = -1f*(getWidth()/2);
        
        //addActor(new GenericActor(0, 0, 1280, 720, new TextureRegion(assetManager.get(AssetsUtil.BACKGROUND, AssetsUtil.TEXTURE)), Color.GRAY));
        
        //Add Player
        initializePlayer(GameInfo.DEFAULT_MAX_FIELDS);
        
        initializeEnvironmentGroups();
        
        initializeStartingScene();

        //Initialize Walls
        walls = new Array<Wall>();

        //Setup our InputListeners
        initializeInputListeners();
        
        //Setup Background Music
        initializeAmbience();

        //Add and Wire-up Button Controls
        initializeButtonControls();

        //Setup Death Overlay
        initializeDeathOverlay();

        //Initialize HUD (Stats, and GameInfo)
        initializeHUD();

        Label.LabelStyle style = new Label.LabelStyle(assetManager.get(AssetsUtil.REXLIA_64, AssetsUtil.BITMAP_FONT), Color.ORANGE);
        comboLabel = new Label(currentCombo + "x Combo!!", style);
        comboLabel.setPosition(player.getX(), player.getTop());
        comboLabel.setVisible(false);
        addActor(comboLabel);


        comboSfx = new ObjectMap<ComboLevels, Sound>();
        comboSfx.put(ComboLevels.NOT_BAD, assetManager.get(AssetsUtil.NOT_BAD, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.GREAT, assetManager.get(AssetsUtil.GREAT, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.AWESOME, assetManager.get(AssetsUtil.AWESOME, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.AMAZING, assetManager.get(AssetsUtil.AMAZING, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.BONKERS, assetManager.get(AssetsUtil.BONKERS, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.RIDICULOUS, assetManager.get(AssetsUtil.RIDICULOUS, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.ATOMIC, assetManager.get(AssetsUtil.ATOMIC, AssetsUtil.SOUND));

        TextureRegion instructionsRegion = new TextureRegion(assetManager.get(AssetsUtil.CONTROLS, AssetsUtil.TEXTURE));
        instructions = new GenericActor(0f, 0f, instructionsRegion.getRegionWidth(), instructionsRegion.getRegionHeight(), instructionsRegion, Color.CYAN);
        addActor(instructions);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if(instructions.isVisible()){

        }else if (!isDead) {
            //Calculate timestep
            millisecondsPassed += delta*1000;
            secondsSinceResourceRegen += delta;

            //processResourceRegens
            processResources();

            particleBubble.update(delta);
        }
        
        if (loading && assetManager.update())
            doneLoading();
        camController.update();
        
        for(int i = 0; i < instances.size; i++){
        	instances.get(i).transform.translate(1*delta, 0, 0);
        	Vector3 loc = new Vector3();
        	instances.get(i).transform.getTranslation(loc);
        	
        	if(loc.x > 10){
        		labToRemove.add(instances.get(i));
        		
        		Model labModel = assetManager.get(AssetsUtil.MODELS_LABWALL, Model.class);
                ModelInstance labInstance = new ModelInstance(labModel);
                labInstance.transform.setToTranslation(0, 0, 0);
                instances.add(labInstance);
        	}
        }
        
        
        for(ModelInstance lab:labToRemove){
        	instances.removeValue(lab, true);
        }
        
        labToRemove.clear();

        //Update GameStats
    }

    @Override
    public void draw() {
        super.draw();

        //3d
        camController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
 
        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
        
        //batch.begin();
        //particleBubble.draw(batch);
        //batch.end();
    }
    
    private void doneLoading() {
        Model labModel = assetManager.get(AssetsUtil.MODELS_LABWALL, Model.class);
        
        float distance = 6.5f;
        
        for (int i = 0; i < 6; i++) {
            ModelInstance labInstance = new ModelInstance(labModel);
            labInstance.transform.setToTranslation(i*distance, 0, 0);
            instances.add(labInstance);
            
            cam.position.set(20f,2f,-15f);
            cam.direction.set(new Vector2(0,0), 1);
            cam.update();
        }
        
        loading = false;
    }

    private void processResources(){
        float secondsBetweenResourceRegen = (millisBetweenWalls/1000)/2;
        if(secondsSinceResourceRegen >= secondsBetweenResourceRegen){//SECONDS_PER_RESOURCE_REGEN){
            regenResources(1);
            //We want to keep any "left-over" time so that we don't get weird timing differences
            secondsSinceResourceRegen = secondsSinceResourceRegen - secondsBetweenResourceRegen;//SECONDS_PER_RESOURCE_REGEN;
        }
    }
    
    private void proccessEnvironmentMovement(float delta){
    	// temp parameters
    	EnvironmentManager.proccessEnvironmentGroupMovement(delta, EnvironmentType.FLOOR.toString());
    	EnvironmentManager.proccessEnvironmentGroupMovement(delta, EnvironmentType.OBSTACLES.toString());
    	EnvironmentManager.proccessEnvironmentGroupMovement(delta, EnvironmentType.PILLAR.toString());
    	EnvironmentManager.proccessEnvironmentGroupMovement(delta, EnvironmentType.BACKFLOOR.toString());
    	EnvironmentManager.proccessEnvironmentGroupMovement(delta, EnvironmentType.WALL.toString());
    	
    	//env.setX(env.getX() + (env.velocity.x * delta));
    }

    private void processWallCollisions() {
        ForceField outerField = player.getOuterForceField();
        for(Wall w:walls){

            //Check for Collisions and apply player/wall information
            if(outerField != null && w.collider.overlaps(outerField.collider)){

                //WHEN OUTERFIELD == WALL we Destroy Both
                //  and increment the score
                if(w.forceFieldType == outerField.forceFieldType){
                    wallsToRemove.add(w);
                    info.score += getWallPointValue(1);

                    //Increment our Combo counter
                    currentCombo += 1;
                    if(highestRunCombo < currentCombo){
                        highestRunCombo = currentCombo;
                    }

                    float explosionVolume = sfxVolume;
                    if(adjustComboLevel(currentCombo)){
                        playComboSoundEffect();
                        explosionVolume /= 2;
                        //On going up a combo level, we will clear all resource usage
                        regenResources(controls.getResourceLevel());

                    }

                    explosionSound.play(explosionVolume);
                }else{
                    //If we hit a bad wall, we reduce your score
                    //  This will discourage jamming out fields like crazy
                    info.score -= 1;
                    info.misses += 1;

                    //Clear our combo
                    currentCombo = 0;
                }

                //Destroy the forcefield if it collides with a wall
                //  The point and wall descrution is calculated above
                player.removeField(outerField);

            }else if(player.collider.overlaps(w.collider)){
                processDeath(w);
            }

            if(w.getX() <= (0f-w.getWidth()/2)){
                wallsToRemove.add(w);
            }
        }
    }
    
    private void generateEnvironment(float delta){
    	generateLabEnvironment(delta);
    }
    
    private void generateLabEnvironment(float delta){
    	// gen first environment
    	if(EnvironmentManager.getEnvironmentGroup(EnvironmentType.FLOOR.toString()) != null){
    		EnvironmentObj prevEnv = EnvironmentManager.getLastEnvironment(EnvironmentType.FLOOR.toString());
    		float nextEnvLoc = ViewportUtil.VP_WIDTH;
    		boolean createEnv = false;
    		
	    	if(prevEnv != null){
		    	nextEnvLoc = prevEnv.getX()+prevEnv.getWidth()/2;
	
		    	if(nextEnvLoc < ViewportUtil.VP_WIDTH + prevEnv.getWidth()){
		    		createEnv = true;
		    	}	
	    	} else{
	    		createEnv = true;
	    	}
	    	
	    	if(createEnv){
		    	EnvironmentObj floor = new EnvironmentObj(nextEnvLoc, 10, 757, 208, new TextureRegion(assetManager.get(AssetsUtil.FLOOR_CONC, AssetsUtil.TEXTURE)), Color.GRAY);
		    	floor.setXVelocity(wallAndFloorVelocity);
		    	EnvironmentManager.addActor(floor, false, EnvironmentType.FLOOR.toString());
	    	}
    	} 
    	
    	// Pillars  	
    	if(millisecondsPassed >= nextGeneration){
	    	EnvironmentObj pillar = new EnvironmentObj(ViewportUtil.VP_WIDTH, 280, 473, 559, new TextureRegion(assetManager.get(AssetsUtil.FLOOR_PILLAR, AssetsUtil.TEXTURE)), Color.GRAY);
	    	pillar.setXVelocity(wallAndFloorVelocity + 100);
	    	
	    	EnvironmentManager.addActor(pillar, false, EnvironmentType.PILLAR.toString());
    	}
    	
    	//WALL
    	if(EnvironmentManager.getEnvironmentGroup(EnvironmentType.WALL.toString()) != null){
    		EnvironmentObj prevEnv = EnvironmentManager.getLastEnvironment(EnvironmentType.WALL.toString());
    		float nextEnvLoc = ViewportUtil.VP_WIDTH;
    		boolean createEnv = false;
    		
	    	if(prevEnv != null){
		    	nextEnvLoc = prevEnv.getX()+prevEnv.getWidth()/2;
	
		    	if(nextEnvLoc < ViewportUtil.VP_WIDTH + prevEnv.getWidth()){
		    		createEnv = true;
		    	}	
	    	} else{
	    		createEnv = true;
	    	}
	    	
	    	if(createEnv){
		    	EnvironmentObj floor = new EnvironmentObj(nextEnvLoc, 310, 473, 250, new TextureRegion(assetManager.get(AssetsUtil.WALL, AssetsUtil.TEXTURE)), Color.GRAY);
		    	floor.setXVelocity(wallAndFloorVelocity + 100);
		    	EnvironmentManager.addActor(floor, false, EnvironmentType.WALL.toString());
	    	}
    	}
    	
    	//FLOOR1
    	if(EnvironmentManager.getEnvironmentGroup(EnvironmentType.BACKFLOOR.toString()) != null){
    		EnvironmentObj prevEnv = EnvironmentManager.getLastEnvironment(EnvironmentType.BACKFLOOR.toString());
    		float nextEnvLoc = ViewportUtil.VP_WIDTH;
    		boolean createEnv = false;
    		
	    	if(prevEnv != null){
		    	nextEnvLoc = prevEnv.getX()+prevEnv.getWidth()/2;
	
		    	if(nextEnvLoc < ViewportUtil.VP_WIDTH + prevEnv.getWidth()){
		    		createEnv = true;
		    	}	
	    	} else{
	    		createEnv = true;
	    	}
	    	
	    	if(createEnv){
		    	EnvironmentObj floor = new EnvironmentObj(nextEnvLoc, 210, 757, 100, new TextureRegion(assetManager.get(AssetsUtil.FLOOR_CONC, AssetsUtil.TEXTURE)), Color.GRAY);
		    	floor.setXVelocity(wallAndFloorVelocity + 50);
		    	EnvironmentManager.addActor(floor, false, EnvironmentType.BACKFLOOR.toString());
	    	}
    	}
    }
    
    private void proccessDestroyedEnvironment(){
    	EnvironmentManager.removeOutOfBoundsEnvironments();
    }
    
    private void processObstacleMovements(float delta){
        for(Wall w:walls){
            w.setX(w.getX() + (w.velocity.x * delta));
        }
    }

    private void processDestroyedWalls() {
        for(Wall w:wallsToRemove){
            walls.removeValue(w, true);
            w.remove();
        }
        wallsToRemove.clear();
    }

    private void generateObstacles() {
        if(millisecondsPassed >= nextGeneration){
            WallPattern wp = getRandomWallPattern();
            //boolean addAfter = warningIndicator == null;
            if(warningIndicator != null){
                warningIndicator.remove();
            }
            warningIndicator = new WarningIndicator(warningIndicatorDimensions[0]+ 10f*walls.size,
                                                       warningIndicatorDimensions[1],
                                                       warningIndicatorDimensions[2],
                                                       warningIndicatorDimensions[3],
                                                       wp,
                                                       Color.RED,
                                                       assetManager.get(AssetsUtil.INDICATOR_SHEET, AssetsUtil.TEXTURE));
            //warningIndicator.lifetime = (millisBetweenWalls/1000)/2;
            addActor(warningIndicator);

            for(int i=0;i<wp.wallCount;i++){
                ForceFieldType fft = wp.forceFields.get(i);
                //FORMULA:  xPos = startX + (N * (wallWidth + wallPadding)
                //          - Where N = NumberOfWalls-1
                Wall w = new Wall(wallDimensions[0] + (i*(wallDimensions[2] + wp.wallPadding)),
                        wallDimensions[1],
                        wallDimensions[2],
                        wallDimensions[3],
                        fft,
                        assetManager.get(AssetsUtil.ANIMATION_ATLAS, AssetsUtil.TEXTURE_ATLAS),
                        getAnimationNameForForceFieldType(fft));
                w.setXVelocity(wallAndFloorVelocity);
                walls.add(w);
                addActor(w);
            }

            nextGeneration += millisBetweenWalls;
        }
    }

    private String getAnimationNameForForceFieldType(ForceFieldType fft){
        String name;
        switch(fft){
            case LIGHTNING:
                name = "newwall/obstacle_blue";
                break;
            case PLASMA:
                name = "newwall/obstacle_green";
                break;
            case LASER:
                name = "newwall/obstacle_red";
                break;
            default:
                name = "walls/light-wall";
                break;
        }

        return name;
    }

    private WallPattern getRandomWallPattern(){
        WallPattern p = new WallPattern(20f);
        Random r = new Random(System.currentTimeMillis());
        //Based on the Max Fields, we generated a new pattern
        int numFields = r.nextInt(info.maxFields) + 1;
        while(numFields > 0){
            ForceFieldType fft = wallTypes[r.nextInt(wallTypes.length)];
            p.addWall(fft);
            numFields--;
        }

        return p;
    }

    private int getWallPointValue(int baseValue){

        int result = baseValue;
        for(int threshold:COMBO_THRESHOLDS){
            if(currentCombo >= threshold){
                result *= 2;
            }else{
                break;
            }
        }

        return result;
    }

    private boolean adjustComboLevel(int latestCombo){
        boolean wasChanged = false;
        if(latestCombo < COMBO_THRESHOLDS[0]){
            currentComboLevel = ComboLevels.NONE;
            wasChanged = false;
        }else if(latestCombo == COMBO_THRESHOLDS[0]){
            currentComboLevel = ComboLevels.NOT_BAD;
            wasChanged = true;
        }else if(latestCombo == COMBO_THRESHOLDS[1]){
            currentComboLevel = ComboLevels.GREAT;
            wasChanged = true;
        }else if(latestCombo == COMBO_THRESHOLDS[2]){
            currentComboLevel = ComboLevels.AWESOME;
            wasChanged = true;
        }else if(latestCombo == COMBO_THRESHOLDS[3]){
            currentComboLevel = ComboLevels.AMAZING;
            wasChanged = true;
        }else if(latestCombo == COMBO_THRESHOLDS[4]){
            currentComboLevel = ComboLevels.BONKERS;
            wasChanged = true;
        }else if(latestCombo == COMBO_THRESHOLDS[5]){
            currentComboLevel = ComboLevels.RIDICULOUS;
            wasChanged = true;
        }else if(latestCombo == COMBO_THRESHOLDS[6]){
            currentComboLevel = ComboLevels.ATOMIC;
            wasChanged = true;
        }
        return wasChanged;
    }

    private void playComboSoundEffect(){
        if(currentComboLevel != ComboLevels.NONE){
            comboSfx.get(currentComboLevel).play(sfxVolume);
        }
    }

    private void adjustDifficulty(){
        int secondsPassedInt  = (int)Math.floor(millisecondsPassed /1000);
        if(secondsPassedInt > 0 && secondsPassedInt != lastWallAdjustTime){
            if(secondsPassedInt%SECONDS_BETWEEN_ADJUSTS == 0){
                if(millisBetweenWalls > MIN_TIME_BETWEEN_WALLS){
                    millisBetweenWalls -= TIME_DECREASE;
                    lastWallAdjustTime = secondsPassedInt;
                }
            }

            if(secondsPassedInt%SECONDS_BETWEEN_DIFF_SHIFT == 0){
                incrementMaxFields();
            }
        }


    }

    private void processDeath(Wall w) {
        //Checking so we only sound once for now

        player.setIsDead(true);

        warningIndicator.remove();
        warningIndicator = null;

        if(!w.equals(collidedWall)){
            wallsToRemove.add(w);
            zapSound.play(sfxVolume);
            isDead = true;
            collidedWall = w;
            music.stop();
        }

        boolean needsSave = false;
        if(info.misses > mostMisses){
            mostMisses = info.misses;
            needsSave = true;
        }
        //Show Data
        //Wait for tap to restart
        if(info.score > highScore){
            highScore = info.score;
            needsSave = true;
        }

        if(highestRunCombo > highestCombo){
            highestCombo = highestRunCombo;
            needsSave = true;
        }

        if(needsSave){
            saveCurrentStats();
        }

        deathOverlay.setSubText("Score: " + info.score + "\t Best Score: " + highScore +
                                "\nMisses: " + info.misses + "\t Most Misses: " + mostMisses +
                                "\nRun Top Combo: " + highestRunCombo + "\t Highest Combo: " + highestCombo);
        deathOverlay.setVisible(true);
    }

    private void saveCurrentStats(){
        gameProcessor.saveGameData(new IDataSaver() {
            @Override
            public void updatePreferences(Preferences prefs) {
                int currentHighScore = prefs.getInteger(GameStats.HIGH_SCORE_KEY);
                if(highScore > currentHighScore){
                    prefs.putInteger(GameStats.HIGH_SCORE_KEY, highScore);
                }

                int currentMostMisses = prefs.getInteger(GameStats.MOST_MISSES_KEY);
                if(mostMisses > currentMostMisses){
                    prefs.putInteger(GameStats.MOST_MISSES_KEY, mostMisses);
                }

                int currentHighestCombo = prefs.getInteger(GameStats.HIGH_COMBO_KEY);
                if(highestCombo > currentHighestCombo){
                    prefs.putInteger(GameStats.HIGH_COMBO_KEY, highestCombo);
                }
            }
        });
    }

    private void resetGame() {
        if(isDead){
            bgVolume = gameProcessor.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY);
            sfxVolume = gameProcessor.getStoredFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY);
            String charSelect = gameProcessor.getStoredString(GameOptions.CHARACTER_SELECT_KEY);
            if(!"".equals(charSelect) && !charSelect.equals(characterSelected)){
                characterSelected = charSelect;
                String aniName = getPlayerAnimationName();
                TextureAtlas atlas = assetManager.get(AssetsUtil.ANIMATION_ATLAS, AssetsUtil.TEXTURE_ATLAS);

                Animation ani = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, atlas.findRegions(aniName));
                player.resetAnimation(ani);
            }

            player.setIsDead(false);
            deathOverlay.setVisible(false);
            info.reset();

            currentComboLevel = ComboLevels.NONE;
            currentCombo = 0;
            highestRunCombo = 0;

            for(Wall w: walls){
                w.remove();
            }
            walls.clear();
            player.clearFields();
            millisBetweenWalls = BASE_TIME_BETWEEN_WALLS;
            isDead = false;

            player.maxFields = info.maxFields;

            controls.restoreAllResourceLevels();
            instructions.setVisible(true);
            music.play();
        }
    }

    private void incrementMaxFields(){
        info.maxFields += 1;
        player.maxFields = info.maxFields;
    }

    private void addField(ForceFieldType fft){
        if(isDead){
            return;
        }

        boolean wasAdded = false;
        int resLevel = controls.getResourceLevel(fft);
        if((controls.getHeatMax() - resLevel) >= player.resourceUsage){
            //Yuck, I don't like this, but I can't come up with an
            //  argument for not doing this. The Stage manages the
            //  state and performs the corresponding actions.
            player.addField(fft);
            controls.incrementHeat(player.resourceUsage);
            wasAdded = true;
        }
        if(wasAdded){
            //TODO: Play Forcefield SoundFX
            powerOnSound.play(sfxVolume);
            player.startShield();

        }else{
            //TODO: Play Resources Limited SoundFX
            zapSound.play(sfxVolume);
        }

    }

    private void addLightningField(){
        addField(ForceFieldType.LIGHTNING);
    }

    private void addPlasmaField(){
        addField(ForceFieldType.PLASMA);
    }

    private void addLaserField(){
        addField(ForceFieldType.LASER);
    }


    public void regenResources(int increment){
        controls.incrementHeat(-increment);
    }

////
///INITIALIZERS
//--------------
    private void initializeVolumes(){
        bgVolume = gameProcessor.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY);
        sfxVolume = gameProcessor.getStoredFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY);
    }

    private void initializeAmbience() {

        initializeVolumes();
        music = assetManager.get(AssetsUtil.DISTORTION_BKG_MUSIC, AssetsUtil.MUSIC);
        music.setVolume(bgVolume);
        music.play();

        zapSound = assetManager.get(AssetsUtil.ZAP_SOUND, AssetsUtil.SOUND);
        powerOnSound = assetManager.get(AssetsUtil.POWER_ON_SOUND, AssetsUtil.SOUND);
        explosionSound = assetManager.get(AssetsUtil.EXPLOSION_SOUND, AssetsUtil.SOUND);
    }

    private void initializeDeathOverlay() {
        BitmapFont mainFont = assetManager.get(AssetsUtil.REXLIA_64, AssetsUtil.BITMAP_FONT);
        BitmapFont subFont = assetManager.get(AssetsUtil.REXLIA_32, AssetsUtil.BITMAP_FONT);
        deathOverlay = new Overlay(0, 0, getWidth(), getHeight(), Color.PURPLE, Color.DARK_GRAY, mainFont, subFont, "You Failed to Escape!", "Score: 0\nBest Score: 0");
        deathOverlay.setVisible(false);

        deathOverlay.setDismissButtonEvent(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resetGame();
            }
        });

        deathOverlay.setHomeButtonEvent(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameProcessor.changeToScreen(BubbleRunnerGame.MENU);
            }
        });
        addActor(deathOverlay);
        deathOverlay.setZIndex(getActors().size - 1);
    }

    private void initializePlayer(int maxFields) {
        String aniName = getPlayerAnimationName();
        String shieldAniName = getPlayerShieldingAnimationName();


        player = new Player(playerDimensions[0],
                playerDimensions[1],
                playerDimensions[2],
                playerDimensions[3],
                assetManager.get(AssetsUtil.ANIMATION_ATLAS, AssetsUtil.TEXTURE_ATLAS),
                aniName);
        player.maxFields = maxFields;
        TextureAtlas atlas = assetManager.get(AssetsUtil.ANIMATION_ATLAS, AssetsUtil.TEXTURE_ATLAS);
        Animation shieldingAnimation = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, atlas.findRegions(shieldAniName));
        player.setShieldingAnimation(shieldingAnimation);
        addActor(player);

        //NOT SURE WHERE THIS GOES
        particleBubble = assetManager.get(AssetsUtil.BUBBLE_PARTICLE, AssetsUtil.PARTICLE);
        particleBubble.start();
        particleBubble.findEmitter("bubble1").setContinuous(true); // reset works for all emitters of particle
    }

    private String getPlayerAnimationName() {
        return characterSelected.equals("Woman") ? "player/Female_Run" : "player/Male_Run";
    }

    private String getPlayerShieldingAnimationName(){
        return characterSelected.equals("Woman") ? "player/Female_Punch" : "player/Male_Punch";
    }

    private void initializeEnvironmentGroups(){
    	addActor(EnvironmentManager.getEnvironmentGroup(EnvironmentType.WALL.toString()));
    	addActor(EnvironmentManager.getEnvironmentGroup(EnvironmentType.BACKFLOOR.toString()));
    	addActor(EnvironmentManager.getEnvironmentGroup(EnvironmentType.FLOOR.toString()));
    	addActor(EnvironmentManager.getEnvironmentGroup(EnvironmentType.PILLAR.toString()));
    	addActor(EnvironmentManager.getEnvironmentGroup(EnvironmentType.OBSTACLES.toString()));
    }
    
    private void initializeStartingScene(){

    	if(EnvironmentManager.getEnvironmentGroup(EnvironmentType.FLOOR.toString()) != null){
	    	EnvironmentObj floor = new EnvironmentObj(-378, 10, 757, 208, new TextureRegion(assetManager.get(AssetsUtil.FLOOR_CONC, AssetsUtil.TEXTURE)), Color.GRAY);
	    	floor.setXVelocity(wallAndFloorVelocity);
	    	EnvironmentManager.addActor(floor, false, EnvironmentType.FLOOR.toString());
	    	
	    	EnvironmentObj floor2 = new EnvironmentObj(-378, 210, 757, 100, new TextureRegion(assetManager.get(AssetsUtil.FLOOR_CONC, AssetsUtil.TEXTURE)), Color.GRAY);
	    	floor2.setXVelocity(wallAndFloorVelocity + 50);
	    	EnvironmentManager.addActor(floor2, false, EnvironmentType.BACKFLOOR.toString());	
    	}
    }

    private void initializeHUD() {
        float infoX = 0f;
        float infoY = ViewportUtil.VP_HEIGHT - HUD_HEIGHT;
        float infoWidth = getWidth();
        float infoHeight = HUD_HEIGHT;
        info = new GameInfo(infoX, infoY, infoWidth, infoHeight, assetManager.get(AssetsUtil.REXLIA_32, AssetsUtil.BITMAP_FONT), controls); //COURIER_FONT_32, AssetsUtil.BITMAP_FONT), controls);
        addActor(info);
    }
    
    private void initializeInputListeners() {
        createAndLeaveListener = new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(Input.Keys.A == keycode || Input.Keys.LEFT == keycode){  //Or Left
                    addLightningField();
                }else if(Input.Keys.S == keycode || Input.Keys.DOWN == keycode){ //Or Down
                    addPlasmaField();
                }else if(Input.Keys.D == keycode || Input.Keys.RIGHT == keycode){  //Or Right
                    addLaserField();
                }else if(Input.Keys.TAB == keycode){
                    KasetagenStateUtil.setDebugMode(!KasetagenStateUtil.isDebugMode());
                }else if(Input.Keys.SPACE == keycode){
                    toggleInstructionsScreen();
                    resetGame();
                }else if(Input.Keys.ESCAPE == keycode){
                    gameProcessor.changeToScreen(BubbleRunnerGame.MENU);
                }
                return super.keyDown(event, keycode);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                toggleInstructionsScreen();
                return super.touchDown(event, x, y, pointer, button);
            }
        };

        this.addListener(createAndLeaveListener);
        currentListener = createAndLeaveListener;
    }

    private void toggleInstructionsScreen() {
        if(instructions.isVisible()){
            instructions.setVisible(false);
        }
    }

    private void initializeButtonControls(){

        controls = new ControlGroup(0, 0, getWidth(), 60f, Color.CYAN);

        TextureRegionDrawable aUp, aDown, aChecked,
                sUp, sDown, sChecked,
                dUp, dDown, dChecked;

        aUp = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.LIGHT_UP, AssetsUtil.TEXTURE)));
        aDown = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.LIGHT_DOWN, AssetsUtil.TEXTURE)));
        aChecked = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.LIGHT_CHECKED, AssetsUtil.TEXTURE)));

        sUp = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.PLASMA_UP, AssetsUtil.TEXTURE)));
        sDown = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.PLASMA_DOWN, AssetsUtil.TEXTURE)));
        sChecked = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.PLASMA_CHECKED, AssetsUtil.TEXTURE)));

        dUp = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.LASER_UP, AssetsUtil.TEXTURE)));
        dDown = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.LASER_DOWN, AssetsUtil.TEXTURE)));
        dChecked = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.LASER_CHECKED, AssetsUtil.TEXTURE)));

        controls.addButton(aUp, aDown, aChecked, new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                addLightningField();
            }
        }, true, ForceFieldType.LIGHTNING);

        controls.addButton(sUp, sDown, sChecked, new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                addPlasmaField();
            }
        }, true, ForceFieldType.PLASMA);

        controls.addButton(dUp, dDown, dChecked, new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                addLaserField();
            }
        }, true, ForceFieldType.LASER);

        controls.setEnergyBar(assetManager.get(AssetsUtil.ENERGY_BAR, AssetsUtil.TEXTURE));
        addActor(controls);
        
        Rectangle rec = new Rectangle(0, 0, 50, 50);
    }

    public void resume(){
        Gdx.app.log("RESUMING", "APPLICATION RESUMING");
        initializeVolumes();
        music.setVolume(bgVolume);
    }

}
