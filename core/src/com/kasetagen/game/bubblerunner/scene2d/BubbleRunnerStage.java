package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kasetagen.engine.IDataSaver;
import com.kasetagen.engine.IGameProcessor;
import com.kasetagen.engine.gdx.scenes.scene2d.ActorDecorator;
import com.kasetagen.engine.gdx.scenes.scene2d.IActorDisposer;
import com.kasetagen.engine.gdx.scenes.scene2d.ICameraModifier;
import com.kasetagen.engine.gdx.scenes.scene2d.KasetagenStateUtil;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.Cinematic;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.CinematicScene;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericActor;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericGroup;
import com.kasetagen.engine.gdx.scenes.scene2d.decorators.OscillatingDecorator;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;
import com.kasetagen.game.bubblerunner.data.GameOptions;
import com.kasetagen.game.bubblerunner.data.GameStats;
import com.kasetagen.game.bubblerunner.data.WallPattern;
import com.kasetagen.game.bubblerunner.scene2d.actor.*;
import com.kasetagen.game.bubblerunner.util.*;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:37 PM
 * This is our main game processing class.
 */
public class BubbleRunnerStage extends BaseStage {

    private enum ComboLevels {
        NONE, NOT_BAD, GREAT, AWESOME, AMAZING, BONKERS, RIDICULOUS, ATOMIC
    }

    private static final long[] shockPulseTimings = new long[] { 0, 100, 100, 100, 100, 100, 100 };
    private static final int[] COMBO_THRESHOLDS = new int[] {10, 20, 30, 50, 70, 100, 150};
//    private static final int[] COMBO_THRESHOLDS = new int[] {5, 10, 12, 15, 18, 20, 21};

    private static final float HUD_HEIGHT = 40f;
    private static final float FLOOR_HEIGHT = 160f;

    private static final int SECONDS_BETWEEN_DIFF_SHIFT = 20;
    private static final long BASE_TIME_BETWEEN_WALLS = 4000L;
    private static final int SECONDS_BETWEEN_ADJUSTS = 5;
    private static final float TIME_DECREASE = 100f;
    private static final float MIN_TIME_BETWEEN_WALLS = 300f;


    private static final float COMBO_BASE_OSCILLATION_RATE = 20f;
    private static final float COMBO_OSCILATION_INCREASE_RATE = 10f;

    //Order of values:  xPos, yPos, width, height
    private static final float INDICATOR_WIDTH = ViewportUtil.VP_WIDTH/4;
    private static final float INDICATOR_HEIGHT = ViewportUtil.VP_HEIGHT/4;

    private static String characterSelected = AnimationUtil.CHARACTER_2;
    private static float[] playerDimensions = new float[] { 100f, 120f, 360f, 360f };

    private static float[] wallDimensions = new float[] {ViewportUtil.VP_WIDTH+FLOOR_HEIGHT,
                                                         FLOOR_HEIGHT, 40f, ViewportUtil.VP_HEIGHT-FLOOR_HEIGHT };
    private static float[] warningIndicatorDimensions = new float[] {ViewportUtil.VP_WIDTH/2 - (INDICATOR_WIDTH/2), ViewportUtil.VP_HEIGHT/2-(INDICATOR_HEIGHT/2),
                                                                     INDICATOR_WIDTH, INDICATOR_HEIGHT};

    private static ForceFieldType[] wallTypes = new ForceFieldType[] { ForceFieldType.LIGHTNING, ForceFieldType.PLASMA, ForceFieldType.LASER};

    private static Vector3 origPos;

    //Delegates
	private IGameProcessor gameProcessor;
	private AssetManager assetManager;
    private TextureAtlas aniAtlas;
    private TextureAtlas spriteAtlas;

    //State Values
    private int highScore = 0;
    private int mostMisses = 0;
    private boolean isDead = false;

    private int currentCombo = 0;
    private int highestRunCombo = 0;
    private int highestCombo = 0;
    private ComboLevels currentComboLevel = ComboLevels.NONE;
    private Label comboLabel;
    private OscillatingDecorator comboDecorator;

    //Obstacle Generation Values
    /**
     * Time Passed is in Seconds
     */
    private float millisecondsPassed = 0f;
    private int lastWallAdjustTime = 0;
    private long nextGeneration = 1000L;
    private long millisBetweenWalls = BASE_TIME_BETWEEN_WALLS;
    private float wallAndFloorVelocity;

    private float secondsSinceResourceRegen = 0f;

    //Actors
    public Player player;
    public ShieldGroup shields;
    public Array<Wall> walls;
    public IActorDisposer wallDisposer;
    public Wall collidedWall = null;
    public GameInfo info;
    public Overlay deathOverlay;
    public Overlay alarmOverlay;
    public ControlGroup controls;

    //Ambiance (Music and Effects
    private WarningIndicator warningIndicator;
    private Music music;
    private Sound zapSound;
    private Sound powerOnSound;
    private Sound explosionSound;
    private Sound screamSound;

    private ObjectMap<ComboLevels, Sound> comboSfx;

    private float bgVolume;
    private float sfxVolume;

    private GenericGroup floorGroup;
    private GenericGroup tunnelGroup;
    private GenericGroup bgGroup;

    private Random rand = new Random(System.currentTimeMillis());

    private float FLR_WIDTH = 417f/2;
    private float FLR_HEIGHT = 415f/2;
    private float FLR_Y = 0f;
    private int floorCount = ((int)Math.ceil(ViewportUtil.VP_WIDTH/FLR_WIDTH)) + 1;
    private float TNL_WIDTH = 947f/2;
    private float TNL_HEIGHT = 500f/2;
    private float TNL_Y = FLR_HEIGHT;
    private int tunnelCount = ((int)Math.ceil(ViewportUtil.VP_WIDTH/TNL_WIDTH) + 1);

    private ActorDecorator offScreenDecorator;

    public BubbleRunnerStage(IGameProcessor gameProcessor){
        super(gameProcessor);
        this.gameProcessor = gameProcessor;
        assetManager = this.gameProcessor.getAssetManager();
        aniAtlas = assetManager.get(AssetsUtil.ANIMATION_ATLAS, AssetsUtil.TEXTURE_ATLAS);
        spriteAtlas = assetManager.get(AssetsUtil.SPRITE_ATLAS, AssetsUtil.TEXTURE_ATLAS);

        Animation introAnimation = new Animation(1f, aniAtlas.findRegions(AtlasUtil.ANI_INTRO));
        cinematic = new Cinematic(0f, 0f, getWidth(), getHeight(), introAnimation, false, Color.DARK_GRAY);

        float duration = 0.3f;
        float startX = 0f;
        float startY = 0f;
        float endX = 50f;
        float endY = 30f;
        float startZoom = 1f;
        float endZoom = 1f;
        CinematicScene scene1 = new CinematicScene(duration, startX, startY, endX, endY, startZoom, endZoom);
        scene1.music = assetManager.get(AssetsUtil.ZAP_SOUND, AssetsUtil.SOUND);
        cinematic.addScene(scene1);

        duration = 0.5f;
        endX = -100f;
        endY = 100f;
        endZoom = 1.25f;
        CinematicScene scene2 = new CinematicScene(duration, startX, startY, endX, endY, startZoom, endZoom);
        scene2.music = assetManager.get(AssetsUtil.AMAZING, AssetsUtil.SOUND);
        cinematic.addScene(scene2);

        duration = 2f;
        endX = 200f;
        endY = 100f;
        endZoom = 0.5f;
        CinematicScene scene3 = new CinematicScene(duration, startX, startY, endX, endY, startZoom, endZoom);
        scene3.music = assetManager.get(AssetsUtil.ZAP_SOUND, AssetsUtil.SOUND);
        cinematic.addScene(scene3);

        duration = 1f;
        endX = 300f;
        endZoom = 1f;
        CinematicScene scene4 = new CinematicScene(duration, startX, startY, endX, endY, startZoom, endZoom);
        scene4.music = assetManager.get(AssetsUtil.AMAZING, AssetsUtil.SOUND);
        cinematic.addScene(scene4);

        endX = 400f;
        CinematicScene scene5 = new CinematicScene(duration, startX, startY, endX, endY, startZoom, endZoom);
        scene5.music = assetManager.get(AssetsUtil.ZAP_SOUND, AssetsUtil.SOUND);
        cinematic.addScene(scene5);

        endX = 500f;
        endY = -100f;
        startZoom = 0.5f;
        endZoom = 0.8f;
        CinematicScene scene6 = new CinematicScene(duration, startX, startY, endX, endY, startZoom, endZoom);
        scene6.music = assetManager.get(AssetsUtil.AMAZING, AssetsUtil.SOUND);
        cinematic.addScene(scene6);

        endX = 600f;
        endY = 100f;
        startZoom = 1f;
        endZoom = 2f;
        CinematicScene scene7 = new CinematicScene(duration, startX, startY, endX, endY, startZoom, endZoom);
        scene7.music = assetManager.get(AssetsUtil.ZAP_SOUND, AssetsUtil.SOUND);
        cinematic.addScene(scene7);

        endX = 700f;
        endY = -200f;
        startZoom = 0.8f;
        endZoom = 1f;
        CinematicScene scene8 = new CinematicScene(duration, startX, startY, endX, endY, startZoom, endZoom);
        scene8.music = assetManager.get(AssetsUtil.AMAZING, AssetsUtil.SOUND);
        cinematic.addScene(scene8);
        addActor(cinematic);
        cinematic.start();

        origPos = new Vector3(getCamera().position);

        offScreenDecorator = new ActorDecorator() {
            @Override
            public void applyAdjustment(Actor actor, float v) {
                if(actor.getX() <= -(actor.getWidth())){
                    ((GenericActor)actor).setIsRemovable(true);
                }
            }
        };
    }

    @Override
    public void onCinematicComplete() {

        //Initialize Privates
        highScore = gameProcessor.getStoredInt(GameStats.HIGH_SCORE_KEY);
        mostMisses = gameProcessor.getStoredInt(GameStats.MOST_MISSES_KEY);
        highestCombo = gameProcessor.getStoredInt(GameStats.HIGH_COMBO_KEY);
        characterSelected = gameProcessor.getStoredString(GameOptions.CHARACTER_SELECT_KEY, AnimationUtil.CHARACTER_2);

        //SET WALL VELOCITY
        wallAndFloorVelocity = -1f*(getWidth()/4);

        floorGroup = new GenericGroup(0f, 0f, ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT, null, Color.BLUE);
        tunnelGroup = new GenericGroup(0f, 0f, ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT, null, Color.RED);
        bgGroup = new GenericGroup(0f, 0f, ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT, null, Color.GREEN);

        addActor(bgGroup);
        addActor(tunnelGroup);
        addActor(floorGroup);

        bgGroup.addActor(new GenericActor(0, 0, 1280, 720, spriteAtlas.findRegion(AtlasUtil.SPRITE_BG), Color.GRAY));

        //Add Player
        initializePlayer(GameInfo.DEFAULT_MAX_FIELDS);

        initializeStartingScene();

        //Initialize Walls
        walls = new Array<Wall>();
        //We use this disposer as our delegate to capture Actor.remove() calls
        //  from our walls so that they can "remove themselves" from the walls array
        wallDisposer = new IActorDisposer() {
            @Override
            public void dispose(Actor actor) {
                if(actor instanceof Wall){
                    walls.removeValue((Wall)actor, false);
                }
            }
        };

        //Setup our InputListeners
        //initializeInputListeners();

        //Setup Background Music
        initializeAmbience();

        //Add and Wire-up Button Controls
        initializeButtonControls();

        //Setup Overlays
        initializeOverlays();


        //Initialize HUD (Stats, and GameInfo)
        initializeHUD();

        Label.LabelStyle style = new Label.LabelStyle(assetManager.get(AssetsUtil.REXLIA_64, AssetsUtil.BITMAP_FONT), Color.ORANGE);
        comboLabel = new Label(currentCombo + "x Combo!!", style);


        DecoratedUIContainer comboContainer = new DecoratedUIContainer(comboLabel);
        comboContainer.setPosition(player.getX() + (comboLabel.getWidth() / 2), player.getTop() + HUD_HEIGHT);

        comboDecorator = new OscillatingDecorator(-3f, 3f, 40f);
        comboContainer.addDecorator(comboDecorator);
        addActor(comboContainer);


        comboSfx = new ObjectMap<ComboLevels, Sound>();
        comboSfx.put(ComboLevels.NOT_BAD, assetManager.get(AssetsUtil.NOT_BAD, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.GREAT, assetManager.get(AssetsUtil.GREAT, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.AWESOME, assetManager.get(AssetsUtil.AWESOME, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.AMAZING, assetManager.get(AssetsUtil.AMAZING, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.BONKERS, assetManager.get(AssetsUtil.BONKERS, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.RIDICULOUS, assetManager.get(AssetsUtil.RIDICULOUS, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.ATOMIC, assetManager.get(AssetsUtil.ATOMIC, AssetsUtil.SOUND));


        super.onCinematicComplete();
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        if(cinematicComplete && !isDead) {
            hideCinematic();
            if(!music.isPlaying()){
                music.play();
            }
            //Calculate timestep
            millisecondsPassed += delta*1000;
            secondsSinceResourceRegen += delta;

            processResources();

            generateEnvironment();

            //Process wall collision events
            processWallCollisions();

            adjustComboOscillation();

            //Add New Wall(s) based on time
            generateObstacles();

            //Increment our obstacle speed
            adjustDifficulty();

            //Adjust Resource Levels

            int index = getActors().size - 1;
            //cinematic.setZIndex(index--);
            deathOverlay.setZIndex(index--);
            controls.setZIndex(index--);
            comboLabel.setZIndex(index--);
            info.setZIndex(index--);
            alarmOverlay.setZIndex(index--);
            shields.setZIndex(index--);
            player.setZIndex(index--);
            for(Wall w:walls){
                w.setZIndex(index--);
            }

            if(currentCombo >= COMBO_THRESHOLDS[0]){
                comboLabel.setText(currentCombo + "x Combo!!");
                comboLabel.setVisible(true);
            }else{
                comboLabel.setVisible(false);
            }
        }else if(walls != null){
            for(Wall w:walls){
                w.setXVelocity(0f);
            }
        }

        //Update GameStats
    }

    @Override
    public void draw() {
        super.draw();
    }

    private void processResources(){
        float secondsBetweenResourceRegen = (millisBetweenWalls/1000)/2;
        if(secondsSinceResourceRegen >= secondsBetweenResourceRegen){
            regenResources(1);
            //We want to keep any "left-over" time so that we don't get weird timing differences
            secondsSinceResourceRegen = secondsSinceResourceRegen - secondsBetweenResourceRegen;
        }
    }

    private void processWallCollisions() {
        ForceField outerField = shields.getOuterForceField();
        for(Wall w:walls){

            //Check for Collisions and apply player/wall information
            if(outerField != null && w.collider.overlaps(outerField.collider)){

                //WHEN OUTERFIELD == WALL we Destroy Both
                //  and increment the score
                if(w.forceFieldType == outerField.forceFieldType){
                    w.setIsRemovable(true);
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
                    addCameraMod(new ICameraModifier() {
                        private float duration = 0.5f;
                        private float elapsedTime = 0f;
                        private float shakeRadius = 5f;
                        private float shakeRate = 720f / 0.5f;


                        @Override
                        public void modify(Camera camera, float delta) {
                            this.elapsedTime += delta;
                            if (!isComplete()) {
                                float x = (float) (shakeRadius * Math.cos(elapsedTime * shakeRate) + origPos.x);
                                float y = (float) (shakeRadius * Math.sin(elapsedTime * shakeRate) + origPos.y);
                                camera.position.set(x, y, camera.position.z);
                            } else {
                                camera.position.set(origPos);
                            }
                        }

                        @Override
                        public boolean isComplete() {
                            return elapsedTime >= duration;
                        }
                    });

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
                shields.removeField(outerField);

            }else if(player.collider.overlaps(w.collider)){
                processDeath(w);
            }

            if(w.getX() <= (0f-w.getWidth()/2)){
                w.setIsRemovable(true);
            }
        }
    }
    
    private void generateEnvironment(){
        if(floorGroup.getChildren().size < floorCount){
            generateNextFloor();
        }

        if(tunnelGroup.getChildren().size < tunnelCount){
            generateNextTunnel();
        }
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
                                                       assetManager.get(AssetsUtil.WARNING_INDICATOR, AssetsUtil.TEXTURE));
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
                        aniAtlas,
                        getAnimationNameForForceFieldType(fft));
                w.setXVelocity(wallAndFloorVelocity);
                w.setDisposer(wallDisposer);
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
                name = AtlasUtil.ANI_WALL_LIGHTNING;
                break;
            case PLASMA:
                name = AtlasUtil.ANI_WALL_PLASMA;
                break;
            case LASER:
                name = AtlasUtil.ANI_WALL_LASER;
                break;
            default:
                name = AtlasUtil.ANI_WALL_LIGHTNING;
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

    private void adjustComboOscillation(){
        float result = COMBO_BASE_OSCILLATION_RATE;
        for(int threshold:COMBO_THRESHOLDS){
            if(currentCombo >= threshold){
                result += COMBO_OSCILATION_INCREASE_RATE;
            }else{
                break;
            }
        }

        comboDecorator.setRotationSpeed(result);
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
        switch(w.forceFieldType){
            case LIGHTNING:
                Gdx.input.vibrate(shockPulseTimings, -1);
                player.setState(PlayerStates.ELECTRO_DEATH, true);
                break;
            case LASER:
                player.setState(PlayerStates.FIRE_DEATH, true);
                break;
            case PLASMA:
                player.setState(PlayerStates.WALL_DEATH, true);
                break;
        }

        player.setIsDead(true);

        if(warningIndicator != null){
            warningIndicator.remove();
            warningIndicator = null;
        }

        setEnvVelocity(0f);

        if(!w.equals(collidedWall)){
            screamSound.play(sfxVolume);
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

        deathOverlay.setSubText("Score: " + info.score + "\n Best Score: " + highScore +
                                "\n\nMisses: " + info.misses + "\n Most Misses: " + mostMisses +
                                "\n\nRun Top Combo: " + highestRunCombo + "\n Highest Combo: " + highestCombo);
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
            Gdx.app.log("CHAR SELECT", "Selected: " + charSelect);
            if(!"".equals(charSelect) && !charSelect.equals(characterSelected)){
                characterSelected = charSelect;
                if(cinematicComplete && player != null){
                    Gdx.app.log("ANIMATIONS", "RESETING PLAYER ANIMATIONS");
                    setPlayerAnimations();
                }
            }

            player.setState(PlayerStates.DEFAULT);
            player.setIsDead(false);
            deathOverlay.setVisible(false);
            info.reset();

            currentComboLevel = ComboLevels.NONE;
            currentCombo = 0;
            highestRunCombo = 0;

            for(Wall w: walls){
                w.setIsRemovable(true);
            }
            shields.clearFields();
            millisBetweenWalls = BASE_TIME_BETWEEN_WALLS;
            isDead = false;

            shields.maxFields = info.maxFields;

            setEnvVelocity(wallAndFloorVelocity);

            controls.restoreAllResourceLevels();
            music.play();
        }
    }

    private void setEnvVelocity(float velocity){
        for(Actor a:floorGroup.getChildren()){
            ((GenericActor)a).velocity.x = velocity;
        }

        for(Actor a:tunnelGroup.getChildren()){
            ((GenericActor)a).velocity.x = velocity;
        }
    }

    private void incrementMaxFields(){
        info.maxFields += 1;
        shields.maxFields = info.maxFields;
    }

    private void addField(ForceFieldType fft){
        if(isDead){
            return;
        }

        boolean wasAdded = false;
        int resLevel = controls.getResourceLevel(fft);
        if((controls.getHeatMax() - resLevel) >= shields.resourceUsage){
            //Yuck, I don't like this, but I can't come up with an
            //  argument for not doing this. The Stage manages the
            //  state and performs the corresponding actions.
            shields.addField(fft);
            controls.incrementHeat(shields.resourceUsage);
            wasAdded = true;
        }
        if(wasAdded){
            powerOnSound.play(sfxVolume);
            player.startShield();

        }else{
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

        zapSound = assetManager.get(AssetsUtil.ZAP_SOUND, AssetsUtil.SOUND);
        powerOnSound = assetManager.get(AssetsUtil.POWER_ON_SOUND, AssetsUtil.SOUND);
        explosionSound = assetManager.get(AssetsUtil.EXPLOSION_SOUND, AssetsUtil.SOUND);
        screamSound = assetManager.get(AssetsUtil.SCREAM, AssetsUtil.SOUND);
    }

    private void initializeOverlays() {
        BitmapFont mainFont = assetManager.get(AssetsUtil.REXLIA_64, AssetsUtil.BITMAP_FONT);
        BitmapFont subFont = assetManager.get(AssetsUtil.REXLIA_32, AssetsUtil.BITMAP_FONT);

        alarmOverlay = new Overlay(0, 0, getWidth(), getHeight(), Color.RED, Color.RED, mainFont, subFont, "", "");
        alarmOverlay.addDecorator(new ActorDecorator() {

            private float max = 0.2f;
            private float min = 0.0f;
            private float current = 0.0f;
            private float flutterSpeed = 0.1f;

            @Override
            public void applyAdjustment(Actor actor, float delta) {
                current += flutterSpeed * delta;
                if(current >= max){
                    current = max;
                    flutterSpeed *= -1;
                }else if(current <= min){
                    current = min;
                    flutterSpeed *= -1;
                }

                ((Overlay)actor).setBackgroundOpacity(current);
            }
        });
        addActor(alarmOverlay);
        alarmOverlay.setZIndex(getActors().size - 1);


        String subText = "Score: 0\n Best Score: 0" +
                         "\n\nMisses: 0\n Most Misses: 0" +
                         "\n\nRun Top Combo: 0\n Highest Combo: 0";
        deathOverlay = new Overlay(0, 0, getWidth(), getHeight(), Color.DARK_GRAY, Color.WHITE, mainFont, subFont, "You Failed to Escape!", subText);

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

    private void setPlayerAnimations(){
        if(player != null){
            String aniName = AnimationUtil.getPlayerAnimationName(characterSelected);
            String shieldAniName = AnimationUtil.getPlayerShieldingAnimationName(characterSelected);
            String electroName = AnimationUtil.getPlayerElectroAnimationName(characterSelected);
            String wallName = AnimationUtil.getPlayerWallAnimationName(characterSelected);
            String fireName = AnimationUtil.getPlayerFireAnimationName(characterSelected);

            Animation defaultPlayerAnimation = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, aniAtlas.findRegions(aniName));
            player.resetAnimation(defaultPlayerAnimation);
            player.addStateAnimation(PlayerStates.DEFAULT, defaultPlayerAnimation);
            Animation shieldingAnimation = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, aniAtlas.findRegions(shieldAniName));
            player.setShieldingAnimation(shieldingAnimation);
            Animation electroAni = new Animation(AnimationUtil.RUNNER_ELECTRO_CYCLE_RATE, aniAtlas.findRegions(electroName));
            player.addStateAnimation(PlayerStates.ELECTRO_DEATH, electroAni);
            Animation fireAni = new Animation(AnimationUtil.RUNNER_FIRE_CYCLE_RATE, aniAtlas.findRegions(fireName));
            player.addStateAnimation(PlayerStates.FIRE_DEATH, fireAni);
            Animation wallAni = new Animation(AnimationUtil.RUNNER_WALL_CYCLE_RATE, aniAtlas.findRegions(wallName));
            player.addStateAnimation(PlayerStates.WALL_DEATH, wallAni);
        }
    }
    private void initializePlayer(int maxFields) {


        player = new Player(playerDimensions[0],
                playerDimensions[1],
                playerDimensions[2],
                playerDimensions[3],
                null);
        //player.maxFields = maxFields;
        setPlayerAnimations();
        addActor(player);

        shields = new ShieldGroup(playerDimensions[0],
                                  playerDimensions[1],
                                  playerDimensions[2],
                                  playerDimensions[3]);
        Animation redShield = new Animation(AnimationUtil.SHIELD_CYCLE_RATE, aniAtlas.findRegions(AtlasUtil.ANI_SHIELD_RED));
        shields.setShieldAnimation(ForceFieldType.LASER, redShield);
        Animation greenShield = new Animation(AnimationUtil.SHIELD_CYCLE_RATE, aniAtlas.findRegions(AtlasUtil.ANI_SHIELD_GREEN));
        shields.setShieldAnimation(ForceFieldType.PLASMA, greenShield);
        Animation blueShield = new Animation(AnimationUtil.SHIELD_CYCLE_RATE, aniAtlas.findRegions(AtlasUtil.ANI_SHIELD_BLUE));
        shields.setShieldAnimation(ForceFieldType.LIGHTNING, blueShield);
        shields.maxFields = maxFields;
        addActor(shields);
    }

    private TextureRegion getFloorTextureRegion(){

        Array<TextureAtlas.AtlasRegion> floors = spriteAtlas.findRegions(AtlasUtil.SPRITE_FLOOR);
        int segment = rand.nextInt(100);
        int index = 0;
        if(segment < 10){
            index = 1;
        }else if(segment < 20){
            index = 2;
        }

        return floors.get(index);
    }

    private TextureRegion getTunnelTextureRegion(){

        Array<TextureAtlas.AtlasRegion> tunnels = spriteAtlas.findRegions(AtlasUtil.SPRITE_WALL);
        int index = 0;
        return tunnels.get(index);
    }

    public void generateNextFloor(){
        int currentFloorCount = floorGroup.getChildren().size;
        float nextPos = currentFloorCount == 0 ? 0f : floorGroup.getChildren().get(currentFloorCount-1).getRight();
        GenericActor floor = new GenericActor(nextPos, FLR_Y, FLR_WIDTH, FLR_HEIGHT, getFloorTextureRegion(), Color.GRAY);
        floor.velocity.x = wallAndFloorVelocity;
        floor.addDecorator(offScreenDecorator);
        floorGroup.addActor(floor);
    }

    public void generateNextTunnel(){
        int currentTunnelCount = tunnelGroup.getChildren().size;
        float nextPos = currentTunnelCount == 0 ? 0f : tunnelGroup.getChildren().get(currentTunnelCount-1).getRight();
        GenericActor tunnel = new GenericActor(nextPos, TNL_Y, TNL_WIDTH, TNL_HEIGHT, getTunnelTextureRegion(), Color.GRAY);
        tunnel.velocity.x = wallAndFloorVelocity;
        tunnel.addDecorator(offScreenDecorator);
        tunnelGroup.addActor(tunnel);
    }
    
    private void initializeStartingScene(){
        for(int i=0;i<floorCount;i++){
            generateNextFloor();
        }

        for(int i=0;i<tunnelCount;i++){
            generateNextTunnel();
        }
    }

    private void initializeHUD() {
        float infoX = 0f;
        float infoY = ViewportUtil.VP_HEIGHT - HUD_HEIGHT;
        float infoWidth = getWidth();
        float infoHeight = HUD_HEIGHT;
        info = new GameInfo(infoX, infoY, infoWidth, infoHeight, assetManager.get(AssetsUtil.REXLIA_32, AssetsUtil.BITMAP_FONT), controls);
        addActor(info);
    }

    @Override
    public boolean keyDown(int keyCode) {

        if(isDead){
            if(Input.Keys.LEFT == keyCode){
                deathOverlay.markDismissed();
            }else if(Input.Keys.RIGHT == keyCode){
                deathOverlay.markHome();
            }else if(Input.Keys.ENTER == keyCode || Input.Keys.SPACE == keyCode){
                if(deathOverlay.isDismissChecked()){
                    hideCinematic();
                    resetGame();
                }else if(deathOverlay.isHomeChecked()){
                    gameProcessor.changeToScreen(BubbleRunnerGame.MENU);
                }
            }
        }

        if (Input.Keys.A == keyCode || Input.Keys.LEFT == keyCode) {  //Or Left
            addLightningField();
        } else if (Input.Keys.S == keyCode || Input.Keys.DOWN == keyCode) { //Or Down
            addPlasmaField();
        } else if (Input.Keys.D == keyCode || Input.Keys.RIGHT == keyCode) {  //Or Right
            addLaserField();
        } else if (Input.Keys.TAB == keyCode) {
            KasetagenStateUtil.setDebugMode(!KasetagenStateUtil.isDebugMode());
        } else if (Input.Keys.SPACE == keyCode) {
            hideCinematic();
            resetGame();
        } else if (Input.Keys.ESCAPE == keyCode) {
            gameProcessor.changeToScreen(BubbleRunnerGame.MENU);
        }
        return super.keyDown(keyCode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        hideCinematic();
        return super.touchDown(screenX, screenY, pointer, button);
    }

    private void hideCinematic() {
        if(cinematic != null && cinematic.isVisible()){
            cinematic.setVisible(false);
        }
    }

    private void initializeButtonControls(){

        controls = new ControlGroup(0, 0, getWidth(), 60f, Color.CYAN);

        TextureRegionDrawable aUp, aDown, aChecked,
                sUp, sDown, sChecked,
                dUp, dDown, dChecked;

        aUp = new TextureRegionDrawable(spriteAtlas.findRegion(AtlasUtil.SPRITE_LIGHT_UP));
        aDown = new TextureRegionDrawable(spriteAtlas.findRegion(AtlasUtil.SPRITE_LIGHT_DOWN));
        aChecked = new TextureRegionDrawable(spriteAtlas.findRegion(AtlasUtil.SPRITE_LIGHT_CHECKED));

        sUp = new TextureRegionDrawable(spriteAtlas.findRegion(AtlasUtil.SPRITE_PLASMA_UP));
        sDown = new TextureRegionDrawable(spriteAtlas.findRegion(AtlasUtil.SPRITE_PLASMA_DOWN));
        sChecked = new TextureRegionDrawable(spriteAtlas.findRegion(AtlasUtil.SPRITE_PLASMA_CHECKED));

        dUp = new TextureRegionDrawable(spriteAtlas.findRegion(AtlasUtil.SPRITE_LASER_UP));
        dDown = new TextureRegionDrawable(spriteAtlas.findRegion(AtlasUtil.SPRITE_LASER_DOWN));
        dChecked = new TextureRegionDrawable(spriteAtlas.findRegion(AtlasUtil.SPRITE_LASER_CHECKED));

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

        controls.setEnergyBar(spriteAtlas.findRegion(AtlasUtil.SPRITE_ENERGY_BAR));
        addActor(controls);
    }

    public void resume(){
        Gdx.app.log("RESUMING", "Stage Resuming");
        initializeVolumes();
        if(music != null){
            music.setVolume(bgVolume);
        }
    }

}
