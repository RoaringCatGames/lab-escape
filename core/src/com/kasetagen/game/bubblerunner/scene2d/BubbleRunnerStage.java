package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kasetagen.engine.gdx.scenes.scene2d.KasetagenStateUtil;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;
import com.kasetagen.game.bubblerunner.data.GameOptions;
import com.kasetagen.game.bubblerunner.data.GameStats;
import com.kasetagen.game.bubblerunner.data.IDataSaver;
import com.kasetagen.game.bubblerunner.data.WallPattern;
import com.kasetagen.game.bubblerunner.delegate.IGameProcessor;
import com.kasetagen.game.bubblerunner.scene2d.actor.*;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;
import com.kasetagen.game.bubblerunner.util.ViewportUtil;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:37 PM
 * This is our main game processing class.
 */
public class BubbleRunnerStage extends BaseStage {


    private static final float HUD_HEIGHT = 40f;
    private static final float FLOOR_HEIGHT = 160f;

    private static final int SECONDS_BETWEEN_DIFF_SHIFT = 30;
    private static final long BASE_TIME_BETWEEN_WALLS = 4000L;
    private static final int SECONDS_BETWEEN_ADJUSTS = 5;
    private static final float TIME_DECREASE = 100f;
    private static final float MIN_TIME_BETWEEN_WALLS = 300f;

    //private static final int SECONDS_PER_RESOURCE_REGEN = 2;


    //Order of values:  xPos, yPos, width, height
    private static final float INDICATOR_WIDTH = ViewportUtil.VP_WIDTH/4;
    private static final float INDICATOR_HEIGHT = ViewportUtil.VP_HEIGHT/4;

    private static float[] playerDimensions = new float[] { 100f, FLOOR_HEIGHT, ViewportUtil.VP_WIDTH/8, (ViewportUtil.VP_HEIGHT/3) }; //old width 160f
    //private static float[] floorDimensions = new float[] { 0f, 0f, ViewportUtil.VP_WIDTH, FLOOR_HEIGHT };
    private static float[] wallDimensions = new float[] {ViewportUtil.VP_WIDTH+FLOOR_HEIGHT,
                                                         FLOOR_HEIGHT, 40f, ViewportUtil.VP_HEIGHT-FLOOR_HEIGHT };
    private static float[] warningIndicatorDimensions = new float[] {ViewportUtil.VP_WIDTH/2 - (INDICATOR_WIDTH/2), ViewportUtil.VP_HEIGHT/2-(INDICATOR_HEIGHT/2),
                                                                     INDICATOR_WIDTH, INDICATOR_HEIGHT};

    private static ForceFieldType[] wallTypes = new ForceFieldType[] { ForceFieldType.LIGHTNING, ForceFieldType.PLASMA, ForceFieldType.LASER};

    //Delegates
	private IGameProcessor gameProcessor;
	private AssetManager assetManager;
    private Batch batch;

    //State Values
    private int highScore = 0;
    private int mostMisses = 0;
    private boolean isDead = false;

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

    private float bgVolume;
    private float sfxVolume;
    
    private enum EnvironmentType {WALL, FLOOR, PILLAR, PLAYER, BACKFLOOR, OBSTACLES};
    
    public BubbleRunnerStage(IGameProcessor gameProcessor){
        super();
        this.gameProcessor = gameProcessor;
    	assetManager = this.gameProcessor.getAssetManager();
    	batch = this.getBatch();

    	EnvironmentManager.initialize(this);
    	
        //Initialize Privates
        wallsToRemove = new Array<Wall>();
        //floorsToRemove = new Array<Environment>();

        highScore = gameProcessor.getStoredInt(GameStats.HIGH_SCORE_KEY);
        mostMisses = gameProcessor.getStoredInt(GameStats.MOST_MISSES_KEY);

        //SET WALL VELOCITY
        wallAndFloorVelocity = -1f*(getWidth()/2);
        
        addActor(new GenericActor(0, 0, 1280, 720, new TextureRegion(assetManager.get(AssetsUtil.BACKGROUND, AssetsUtil.TEXTURE)), Color.GRAY));
        
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
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (!isDead) {
            //Calculate timestep
            millisecondsPassed += delta*1000;
            secondsSinceResourceRegen += delta;

            //processResourceRegens
            processResources();
            
            proccessEnvironmentMovement(delta);
            
            proccessDestroyedEnvironment();
            
            generateEnvironment(delta);
            
            //Move Walls Closer based on Speed
            processObstacleMovements(delta);

            //Process wall collision events
            processWallCollisions();

            //Any walls marked for removal need to be
            //  dropped and disposed of
            processDestroyedWalls();
            
            //Add New Wall(s) based on time
            generateObstacles();

            //Increment our obstacle speed
            adjustDifficulty();

            //Adjust Resource Levels

            int index = getActors().size - 1;
            info.setZIndex(index--);
            player.setZIndex(index--);
            for(Wall w:walls){
                w.setZIndex(index--);
            }
        }
        particleBubble.update(delta);
		particleBubble.setPosition(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 4);
        //Update GameStats
    }

    @Override
    public void draw() {     
        super.draw();
        
        batch.begin();
        //particleBubble.draw(batch);
        batch.end();
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
                    info.score += 1;
                    explosionSound.play(sfxVolume);
                }else{
                    //If we hit a bad wall, we reduce your score
                    //  This will discourage jamming out fields like crazy
                    info.score -= 1;
                    info.misses += 1;
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
    		Environment prevEnv = EnvironmentManager.getLastEnvironment(EnvironmentType.FLOOR.toString());
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
		    	Environment floor = new Environment(nextEnvLoc, 10, 757, 208, new TextureRegion(assetManager.get(AssetsUtil.FLOOR_CONC, AssetsUtil.TEXTURE)), Color.GRAY);
		    	floor.setXVelocity(wallAndFloorVelocity);
		    	EnvironmentManager.addActor(floor, false, EnvironmentType.FLOOR.toString());
	    	}
    	} 
    	
    	// Pillars  	
    	if(millisecondsPassed >= nextGeneration){
	    	Environment pillar = new Environment(ViewportUtil.VP_WIDTH, 280, 473, 559, new TextureRegion(assetManager.get(AssetsUtil.FLOOR_PILLAR, AssetsUtil.TEXTURE)), Color.GRAY);
	    	pillar.setXVelocity(wallAndFloorVelocity + 100);
	    	
	    	EnvironmentManager.addActor(pillar, false, EnvironmentType.PILLAR.toString());
    	}
    	
    	//WALL
    	if(EnvironmentManager.getEnvironmentGroup(EnvironmentType.WALL.toString()) != null){
    		Environment prevEnv = EnvironmentManager.getLastEnvironment(EnvironmentType.WALL.toString());
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
		    	Environment floor = new Environment(nextEnvLoc, 310, 473, 250, new TextureRegion(assetManager.get(AssetsUtil.WALL, AssetsUtil.TEXTURE)), Color.GRAY);
		    	floor.setXVelocity(wallAndFloorVelocity + 100);
		    	EnvironmentManager.addActor(floor, false, EnvironmentType.WALL.toString());
	    	}
    	}
    	
    	//FLOOR1
    	if(EnvironmentManager.getEnvironmentGroup(EnvironmentType.BACKFLOOR.toString()) != null){
    		Environment prevEnv = EnvironmentManager.getLastEnvironment(EnvironmentType.BACKFLOOR.toString());
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
		    	Environment floor = new Environment(nextEnvLoc, 210, 757, 100, new TextureRegion(assetManager.get(AssetsUtil.FLOOR_CONC, AssetsUtil.TEXTURE)), Color.GRAY);
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
                name = "newwall/obstacle_blue";//"walls/light-wall";
                break;
            case PLASMA:
                name = "newwall/obstacle_green";//"walls/plasma-wall";
                break;
            case LASER:
                name = "newwall/obstacle_red";//"walls/discharge-wall";
                break;
            default:
                name = "walls/light-wall";
                break;
        }

        return name;
    }

//    private TextureRegion getTextureRegionForForceFieldType(ForceFieldType fft) {
//        Texture texture;
//        switch(fft){
//            case LIGHTNING:
//                texture = assetManager.get(AssetsUtil.LIGHTNING_WALL, AssetsUtil.TEXTURE);
//                break;
//            case PLASMA:
//                texture = assetManager.get(AssetsUtil.PLASMA_WALL, AssetsUtil.TEXTURE);
//                break;
//            case LASER:
//                texture = assetManager.get(AssetsUtil.LASER_WALL, AssetsUtil.TEXTURE);
//                break;
//            default:
//                texture = assetManager.get(AssetsUtil.LIGHTNING_WALL, AssetsUtil.TEXTURE);
//                break;
//        }
//
//        return new TextureRegion(texture);
//    }

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

        if(needsSave){
            saveCurrentStats();
        }
        deathOverlay.setSubText("Score: " + info.score + "\t Misses: " + info.misses + "\nBest Score: " + highScore + "\t Most Misses: " + mostMisses);
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
            }
        });
    }

    private void resetGame() {
        if(isDead){
            player.setIsDead(false);
            deathOverlay.setVisible(false);
            info.reset();
            for(Wall w: walls){
                w.remove();
            }
            walls.clear();
            player.clearFields();
            millisBetweenWalls = BASE_TIME_BETWEEN_WALLS;
            isDead = false;

            player.maxFields = info.maxFields;

            controls.restoreAllResourceLevels();
            music.play();
        }
    }

    private void incrementMaxFields(){
        info.maxFields += 1;
        player.maxFields = info.maxFields;
    }

    public void toggleListener(){

        player.clearFields();
        if(currentListener == createAndLeaveListener){
            this.removeListener(createAndLeaveListener);
            this.addListener(keysReleasedListener);
            currentListener = keysReleasedListener;
        }else{
            this.removeListener(keysReleasedListener);
            this.addListener(createAndLeaveListener);
            currentListener = createAndLeaveListener;
        }
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
        music = assetManager.get(AssetsUtil.ALT_BG_MUSIC, AssetsUtil.MUSIC);
        music.setVolume(bgVolume);
        music.play();

        zapSound = assetManager.get(AssetsUtil.ZAP_SOUND, AssetsUtil.SOUND);
        powerOnSound = assetManager.get(AssetsUtil.POWER_ON_SOUND, AssetsUtil.SOUND);
        explosionSound = assetManager.get(AssetsUtil.EXPLOSION_SOUND, AssetsUtil.SOUND);
    }

    private void initializeDeathOverlay() {
        BitmapFont mainFont = assetManager.get(AssetsUtil.COURIER_FONT_32, AssetsUtil.BITMAP_FONT);
        BitmapFont subFont = assetManager.get(AssetsUtil.COURIER_FONT_18, AssetsUtil.BITMAP_FONT);
        deathOverlay = new Overlay(0, 0, getWidth(), getHeight(), Color.PURPLE, Color.WHITE, mainFont, subFont, "You Failed to Escape!", "Score: 0\nBest Score: 0");
        deathOverlay.setVisible(false);

        deathOverlay.setDismissButtonEvent(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resetGame();
            }
        });
        addActor(deathOverlay);
        deathOverlay.setZIndex(getActors().size - 1);
    }

    private void initializePlayer(int maxFields) {
        player = new Player(playerDimensions[0],
                playerDimensions[1],
                playerDimensions[2],
                playerDimensions[3],
                assetManager.get(AssetsUtil.ANIMATION_ATLAS, AssetsUtil.TEXTURE_ATLAS));
        player.maxFields = maxFields;
        addActor(player);

        //NOT SURE WHERE THIS GOES
        particleBubble = assetManager.get(AssetsUtil.BUBBLE_PARTICLE, AssetsUtil.PARTICLE);
        particleBubble.start();
        particleBubble.findEmitter("bubble1").setContinuous(true); // reset works for all emitters of particle
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
	    	Environment floor = new Environment(-378, 10, 757, 208, new TextureRegion(assetManager.get(AssetsUtil.FLOOR_CONC, AssetsUtil.TEXTURE)), Color.GRAY);
	    	floor.setXVelocity(wallAndFloorVelocity);
	    	EnvironmentManager.addActor(floor, false, EnvironmentType.FLOOR.toString());
	    	
	    	Environment floor2 = new Environment(-378, 210, 757, 100, new TextureRegion(assetManager.get(AssetsUtil.FLOOR_CONC, AssetsUtil.TEXTURE)), Color.GRAY);
	    	floor2.setXVelocity(wallAndFloorVelocity + 50);
	    	EnvironmentManager.addActor(floor2, false, EnvironmentType.BACKFLOOR.toString());	
    	}
    }

    private void initializeHUD() {
        float infoX = 0f;
        float infoY = ViewportUtil.VP_HEIGHT - HUD_HEIGHT;
        float infoWidth = getWidth();
        float infoHeight = HUD_HEIGHT;
        info = new GameInfo(infoX, infoY, infoWidth, infoHeight, assetManager.get(AssetsUtil.COURIER_FONT_32, AssetsUtil.BITMAP_FONT), controls);
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
                    //toggleListener();
                    KasetagenStateUtil.setDebugMode(!KasetagenStateUtil.isDebugMode());
                }else if(Input.Keys.SPACE == keycode){
                    resetGame();
                }else if(Input.Keys.ESCAPE == keycode){
                    gameProcessor.changeToScreen(BubbleRunnerGame.MENU);
                }
                return super.keyDown(event, keycode);
            }
        };

        keysReleasedListener = new InputListener(){

            boolean isADown = false;
            boolean isSDown = false;
            boolean isDDown = false;

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(Input.Keys.A == keycode && !isADown){
                    player.addField(ForceFieldType.LIGHTNING, 0);
                    isADown = true;
                }else if(Input.Keys.S == keycode && !isSDown){
                    player.addField(ForceFieldType.PLASMA, 0);
                    isSDown = true;
                }else if(Input.Keys.D == keycode & !isDDown){
                    player.addField(ForceFieldType.LASER, 0);
                    isDDown = true;
                }else if(Input.Keys.TAB == keycode){
                    //toggleListener();
                    KasetagenStateUtil.setDebugMode(!KasetagenStateUtil.isDebugMode());
                }else if(Input.Keys.SPACE == keycode){
                    resetGame();
                }
                return super.keyDown(event, keycode);
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if(Input.Keys.A == keycode && isADown){
                    player.removeField(ForceFieldType.LIGHTNING);
                    isADown = false;
                }else if(Input.Keys.S == keycode && isSDown){
                    player.removeField(ForceFieldType.PLASMA);
                    isSDown = false;
                }else if(Input.Keys.D == keycode & isDDown){
                    player.removeField(ForceFieldType.LASER);
                    isDDown = false;
                }
                return super.keyDown(event, keycode);
            }
        };

        this.addListener(createAndLeaveListener);
        currentListener = createAndLeaveListener;
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
    }

    public void resume(){
        Gdx.app.log("RESUMING", "APPLICATION RESUMING");
        initializeVolumes();
        music.setVolume(bgVolume);
    }

}
