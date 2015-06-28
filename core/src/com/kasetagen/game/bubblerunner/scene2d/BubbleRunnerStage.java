package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.Application;
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
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kasetagen.engine.IDataSaver;
import com.kasetagen.engine.IGameProcessor;
import com.kasetagen.engine.gdx.scenes.scene2d.ActorDecorator;
import com.kasetagen.engine.gdx.scenes.scene2d.IActorDisposer;
import com.kasetagen.engine.gdx.scenes.scene2d.ICameraModifier;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.*;
import com.kasetagen.engine.gdx.scenes.scene2d.decorators.ShakeDecorator;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;
import com.kasetagen.game.bubblerunner.data.*;
import com.kasetagen.game.bubblerunner.scene2d.actor.*;
import com.kasetagen.game.bubblerunner.util.*;

import java.util.Iterator;
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
        NONE, NOT_BAD, GREAT, AWESOME, AMAZING, UNBELIEVABLE, PERFECT, UNSTOPPABLE
    }

    private static final long[] shockPulseTimings = new long[] { 0, 100, 100, 100, 100, 100, 100 };
    private static final int[] COMBO_THRESHOLDS = new int[] {10, 20, 30, 50, 70, 100, 150};
    //private static final int[] COMBO_THRESHOLDS = new int[] {5, 10, 12, 15, 18, 20, 21};

    private static final float HUD_HEIGHT = 40f;

    private static final int MAXIMUM_BODY_LIMIT = 40;

    private static final int SECONDS_BETWEEN_DIFF_SHIFT = 20;
    private static final long BASE_TIME_BETWEEN_WALLS = 4000L;
    private static final int SECONDS_BETWEEN_ADJUSTS = 5;
    private static final float TIME_DECREASE = 100f;
    private static final float MIN_TIME_BETWEEN_WALLS = 300f;

    private static final float WALL_CYCLE_RATE = 1f/2f;
    private static final float WALL_BREAK_CYCLE_RATE = 1f/10f;
    private static final float TESLA_CYCLE_RATE = 1/15f;
    private static final float LASER_CYCLE_RATE = 1f/4f;

    private static final float COMBO_BASE_OSCILLATION_RATE = 1f;//20f;
    private static final float COMBO_OSCILATION_INCREASE_RATE = -0.1f;//10f;

    //Order of values:  xPos, yPos, width, height

    private static String characterSelected = AnimationUtil.CHARACTER_2;
    private static float[] playerDimensions = new float[] { 100f, 100f, 360f, 360f };
    private static float PUKE_W = 425f/2f;
    private static float PUKE_H = 225f/2f;
    private static float PUKE_M_X = playerDimensions[0] + 42f;
    private static float PUKE_M_Y = playerDimensions[1] + 183f;
    private static float PUKE_F_X = playerDimensions[0] + 40f;
    private static float PUKE_F_Y = playerDimensions[1] + 175f;

    private static float WALL_START_X = ViewportUtil.VP_WIDTH + 700f;//160f;
    private static float WALL_Y = 0f;
    private static float WALL_WIDTH = 525f/2f;
    private static float WALL_HEIGHT = 1440f/2f;
    private static float[] wallDimensions = new float[] { WALL_START_X, WALL_Y, WALL_WIDTH, WALL_HEIGHT };
    private static float[] wallColliderDimensions = new float[] { 40f, ViewportUtil.VP_HEIGHT };

    private static float SHATTER_WIDTH = 1361f/2f;
    private static float SHATTER_HEIGHT = 1224f/2f;

    private static ForceFieldType[] wallTypes = new ForceFieldType[] { ForceFieldType.LIGHTNING, ForceFieldType.PLASMA, ForceFieldType.LASER};

    private static Vector3 origPos;

    public static final int DEFAULT_MIN_FIELDS = 1;
    public static final int DEFAULT_MAX_FIELDS = 2;
    private static int MIN_FIELDS = DEFAULT_MIN_FIELDS;
    private static int MAX_FIELDS = DEFAULT_MAX_FIELDS;


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
    private DecoratedUIContainer comboContainer;
    //private OscillatingDecorator comboDecorator;
    private ShakeDecorator comboDecorator;

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
    public AnimatedActor puke;
    public ShieldGroup shields;
    public Array<Wall> walls;
    public Wall collidedWall = null;
    public GameInfo info;
    public Overlay deathOverlay;
    public Overlay alarmOverlay;
    public ControlGroup leftControls;
    public ControlGroup rightControls;
    public ResourceLimitation bodyLimit;

    //Ambiance (Music and Effects
    //private ShakeDecorator warningShaker;
    private Array<Indicator> indicators;
    private IActorDisposer indicatorDisposer;
    private WarningIndicator warningIndicator;
    private Music music;
    private Sound edisonSickSound;
    private Sound edynSickSound;
    private Sound shieldUpSound;
    private Sound badShieldSound;
    private Sound breakElectricSound;
    private Sound breakLaserSound;
    private Sound breakGlassSound;
    private Sound dieGlassSound;
    private Sound dieShockSound;
    private Sound dieFireSound;
    private Sound dieEdisonSound;
    private Sound dieEdynSound;

    private ObjectMap<ComboLevels, Sound> comboSfx;

    private float bgVolume;
    private float sfxVolume;

    private GenericGroup floorGroup;
    private GenericGroup floorAccentGroup;
    private GenericGroup tunnelGroup;
    private GenericGroup bgGroup;
    private GenericGroup leftWallGroup;
    private GenericGroup rightWallGroup;

    private boolean tunnelGroupHasSpecialTunnel = false;

    private Random rand = new Random(System.currentTimeMillis());

    private float FLOOR_WIDTH = 420f/2f;
    private float FLOOR_HEIGHT = 420f/2f;
    private float FLOOR_X_OFFSET = 0f;//-152f/2f;
    private float FLOOR_Y = 0f;
    private int floorCount = ((int)Math.ceil(ViewportUtil.VP_WIDTH/(FLOOR_WIDTH + FLOOR_X_OFFSET))) + 1;

    private float ACCENT_WIDTH = 1000f/2f;
    private float ACCENT_HEIGHT = 57f/2f;
    private float ACCENT_Y = 0f;
    private int accentCount = 2* (((int)Math.ceil(ViewportUtil.VP_WIDTH/ACCENT_WIDTH)) + 1);

    private float TNL_WIDTH = 1024f/2;
    private float TNL_HEIGHT = 1020f/2;
    private float TNL_Y = FLOOR_HEIGHT;
    private int tunnelCount = ((int)Math.ceil(ViewportUtil.VP_WIDTH/TNL_WIDTH) + 1);

    private ActorDecorator offScreenDecorator;

    private ActorDecorator resumeAnimationOnScreenDecorator;

    public BubbleRunnerStage(IGameProcessor gameProcessor){
        super(gameProcessor);
        this.gameProcessor = gameProcessor;
        assetManager = this.gameProcessor.getAssetManager();
        aniAtlas = assetManager.get(AssetsUtil.ANIMATION_ATLAS, AssetsUtil.TEXTURE_ATLAS);
        spriteAtlas = assetManager.get(AssetsUtil.SPRITE_ATLAS, AssetsUtil.TEXTURE_ATLAS);

        Animation introAnimation = new Animation(1f, aniAtlas.findRegions(AtlasUtil.ANI_INTRO));
        cinematic = new Cinematic(0f, 0f, getWidth(), getHeight(), introAnimation, false, Color.DARK_GRAY);
        cinematic.setRequiresContinue(true);

        float duration = 2f;
        float startX = 0f;
        float startY = -720f;
        float endX = 0f;
        float endY = 0f;
        float startZoom = 1f;
        float endZoom = 1f;
        CinematicScene scene1 = new CinematicScene(duration, startX, startY, endX, endY, startZoom, endZoom);
        cinematic.addScene(scene1);

        addActor(cinematic);
        cinematic.start();

        origPos = new Vector3(getCamera().position);


        offScreenDecorator = new ActorDecorator() {
            @Override
            public void applyAdjustment(Actor actor, float v) {
                if(actor.getX() <= -(actor.getWidth())){
                    ((GenericActor)actor).setIsRemovable(true);
                    //if We are removing a special segment we flag that we can add one
                    if(actor instanceof TunnelSegment && ((TunnelSegment)actor).isSpecial()){
                        tunnelGroupHasSpecialTunnel = false;
                    }
                }
            }
        };

        resumeAnimationOnScreenDecorator = new ActorDecorator() {
            @Override
            public void applyAdjustment(Actor actor, float v) {
                if(actor instanceof AnimatedActor){
                    if(actor.getX() + actor.getOriginX() <= getWidth()){
                        ((AnimatedActor)actor).resume();
                    }
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

        bodyLimit = new ResourceLimitation(MAXIMUM_BODY_LIMIT);
        //SET WALL VELOCITY
        wallAndFloorVelocity = -1f*(getWidth()/2f);

        floorGroup = new GenericGroup(0f, 0f, ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT, null, Color.BLUE);
        floorAccentGroup = new GenericGroup(0f, 0f, ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT, null, Color.BLUE);
        tunnelGroup = new GenericGroup(0f, 0f, ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT, null, Color.RED);
        bgGroup = new GenericGroup(0f, 0f, ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT, null, Color.GREEN);
        leftWallGroup = new GenericGroup(0f, 0f, ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT, null, Color.GREEN);

        addActor(bgGroup);
        addActor(tunnelGroup);
        addActor(floorGroup);
        addActor(floorAccentGroup);
        addActor(leftWallGroup);

        //TODO: Put bg sky?
        //bgGroup.addActor(new GenericActor(0, 0, 1280, 720, spriteAtlas.findRegion(AtlasUtil.SPRITE_BG), Color.GRAY));

        //Add Player
        initializePlayer();
        rightWallGroup = new GenericGroup(0f, 0f, ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT, null, Color.GREEN);
        addActor(rightWallGroup);

        initializeStartingScene();

        //Initialize Walls
        indicatorDisposer = new IActorDisposer() {
            @Override
            public void dispose(Actor actor) {
                indicators.removeValue((Indicator)actor, true);
            }
        };

        indicators = new Array<Indicator>();
        walls = new Array<Wall>();

        //Setup Background Music
        initializeAmbience();

        //Add and Wire-up Button Controls
        initializeButtonControls();

        //Setup Overlays
        initializeOverlays();


        //Initialize HUD (Stats, and GameInfo)
        initializeHUD();

        Label.LabelStyle style = new Label.LabelStyle(assetManager.get(AssetsUtil.NEUROPOL_64, AssetsUtil.BITMAP_FONT), Color.ORANGE);
        comboLabel = new Label(currentCombo + "x Combo!!", style);


        comboContainer = new DecoratedUIContainer(comboLabel);
        comboContainer.setPosition((getWidth()/2f), player.getTop() + HUD_HEIGHT);

        comboDecorator = new ShakeDecorator(5f, 10f, 0.5f, 1f);
        comboContainer.addDecorator(comboDecorator);
        addActor(comboContainer);


        comboSfx = new ObjectMap<ComboLevels, Sound>();
        comboSfx.put(ComboLevels.NOT_BAD, assetManager.get(AssetsUtil.NOT_BAD, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.GREAT, assetManager.get(AssetsUtil.GREAT, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.AWESOME, assetManager.get(AssetsUtil.AWESOME, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.AMAZING, assetManager.get(AssetsUtil.AMAZING, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.UNBELIEVABLE, assetManager.get(AssetsUtil.UNBELIEVABLE, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.PERFECT, assetManager.get(AssetsUtil.PERFECT, AssetsUtil.SOUND));
        comboSfx.put(ComboLevels.UNSTOPPABLE, assetManager.get(AssetsUtil.UNSTOPPABLE, AssetsUtil.SOUND));


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

            //Remove any walls from the array to
            //  let them be destroyed.
            Iterator<Wall> itr = walls.iterator();
            while(itr.hasNext()){
                Wall w = itr.next();
                if(w.isRemovable()){
                    itr.remove();
                }
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

            //Mark any indicators as removable
            for(Indicator i:indicators){
                if(i.getRight() < 0){
                    i.setIsRemovable(true);
                }
            }

            //Adjust Resource Levels
            if(puke.isVisible() && puke.isAnimationComplete()){
                puke.setVisible(false);
            }

            int index = getActors().size - 1;
            cinematic.setZIndex(index--);
            deathOverlay.setZIndex(index--);
            leftControls.setZIndex(index--);
            rightControls.setZIndex(index--);
            comboLabel.setZIndex(index--);
            info.setZIndex(index--);
            alarmOverlay.setZIndex(index--);
            comboContainer.setZIndex(index--);
            if(warningIndicator != null){
                warningIndicator.setZIndex(index--);
            }
            rightWallGroup.setZIndex(index--);
            shields.setZIndex(index--);
            puke.setZIndex(index--);
            player.setZIndex(index--);
            leftWallGroup.setZIndex(index--);
            floorAccentGroup.setZIndex(index--);
            floorGroup.setZIndex(index--);
            tunnelGroup.setZIndex(index--);

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

            for(Indicator i:indicators){
                i.velocity.x = 0f;
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

    private ForceField findCollidingForceField(Wall w){
        ForceField foundField = null;
        if(w != null){
            for(ForceField f : shields.getFields()){
                if(w.collider.overlaps(f.collider)){
                    foundField = f;
                    break;
                }
            }
        }

        return foundField;
    }

    private void processWallCollisions() {
        for(Wall w:walls){
            ForceField collidingField = findCollidingForceField(w);
            if("BREAKING".equals(w.getCurrentState())){
                //Do nothing, let it slide around us
            }else if(collidingField != null){

                //WHEN OUTERFIELD == WALL we Destroy Both
                //  and increment the score
                if(w.forceFieldType == collidingField.forceFieldType){
                    w.setState("BREAKING", true);
                    if(w.forceFieldType == ForceFieldType.PLASMA){
                        w.setIsLooping(false);
                    }
                    info.score += getWallPointValue(1);

                    //Increment our Combo counter
                    currentCombo += 1;
                    if(highestRunCombo < currentCombo){
                        highestRunCombo = currentCombo;
                    }

                    float breakVolume = sfxVolume;
                    if(adjustComboLevel(currentCombo)){
                        playComboSoundEffect();
                        breakVolume /= 2;
                        //On going up a combo level, we will clear all resource usage
                        regenResources(bodyLimit.getResourceLevel());
                    }

                    switch(w.forceFieldType){
                        case LIGHTNING:
                            breakElectricSound.play(breakVolume);
                            break;
                        case LASER:
                            breakLaserSound.play(breakVolume);
                            break;
                        case PLASMA:
                            breakGlassSound.play(breakVolume);
                            break;
                    }

                    applyCameraShake();

                }else{
                    //If we hit a bad wall, we reduce your score
                    //  This will discourage jamming out fields like crazy
                    info.score -= 1;
                    info.misses += 1;

                    //Clear our combo
                    currentCombo = 0;

                    badShieldSound.play(sfxVolume);
                }

                //Destroy the forcefield if it collides with a wall
                //  The point and wall descrution is calculated above
                shields.removeField(collidingField);
            }else if(player.collider.overlaps(w.collider)){
                processDeath(w);
            }

            //THIS IS UGLY
            if(w.getX() <= (0f-w.getWidth())){
                w.setIsRemovable(true);
            }

            Array<ForceField> fields = new Array<ForceField>();
            for(ForceField f : shields.getFields()){
                if(f.getX() > ViewportUtil.VP_WIDTH){
                    fields.add(f);
                }
            }
            for(ForceField f:fields){
                shields.removeField(f);
            }
        }
    }

    private void applyCameraShake() {
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
    }

    private void generateEnvironment(){
        if(floorGroup.getChildren().size < floorCount){
            generateNextFloor();
        }

        if(floorAccentGroup.getChildren().size < accentCount){
            generteNextFloorAccent();
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


            float xPos = getWidth();
            if(indicators.size > 0){
                float lastIndicatorRight = indicators.get(indicators.size -1).getRight();
                if(xPos <= lastIndicatorRight){
                    xPos = lastIndicatorRight + 10f;
                }
            }
            float yPos = 600f;

            float x, y, w, h;
            x = wallDimensions[0];
            //If X is < endingWall.getRight() Adjust
            if(walls.size > 0){
                float lastWallRight = walls.get(walls.size - 1).getRight();
                if(x <= lastWallRight){
                    //x += wp.wallPadding;
                    x = lastWallRight + wp.wallPadding;
                }
            }
            y = wallDimensions[1];
            w = wallDimensions[2];
            h = wallDimensions[3];
            float timeBeforeWallHitsPlayer = (((x+w)-player.getRight())/wallAndFloorVelocity);
            float indicatorSpeed = xPos / timeBeforeWallHitsPlayer;
            for(int i=0;i<wp.wallCount;i++){
                ForceFieldType fft = wp.forceFields.get(i);

                float keyFrame = rand.nextInt(5000) * WALL_CYCLE_RATE;
                float cycleRate = getAnimationCycleRateForForceFieldType(fft, false);
                Animation leftAni = new Animation(cycleRate, aniAtlas.findRegions(getAnimationNameForForceFieldType(fft, true, false)));
                AnimatedActor leftWall = new AnimatedActor(x + (i*(w + wp.wallPadding)), y, w/2, h, leftAni,keyFrame);

                Animation rightAni = new Animation(cycleRate, aniAtlas.findRegions(getAnimationNameForForceFieldType(fft, false, false)));
                AnimatedActor rightWall = new AnimatedActor(leftWall.getRight(), y, w/2, h, rightAni, keyFrame);

                GenericActor collider = new GenericActor(leftWall.getRight()-(wallColliderDimensions[0]/2), y, wallColliderDimensions[0], wallColliderDimensions[1], null, Color.BLACK);
                AnimatedActor flourish = new AnimatedActor(collider.getRight(), y, SHATTER_WIDTH, SHATTER_HEIGHT, null, 0f);


                leftWall.addDecorator(offScreenDecorator);
                leftWall.addDecorator(offScreenDecorator);
                flourish.addDecorator(offScreenDecorator);
                collider.addDecorator(offScreenDecorator);

                leftWallGroup.addActor(leftWall);
                //Put the flourish behind the wall
                rightWallGroup.addActor(flourish);
                rightWallGroup.addActor(rightWall);
                addActor(collider);

                float breakCycleRate = getAnimationCycleRateForForceFieldType(fft, true);
                Animation leftBreakAni = new Animation(breakCycleRate, aniAtlas.findRegions(getAnimationNameForForceFieldType(fft, true, true)));
                Animation rightBreakAni = new Animation(breakCycleRate, aniAtlas.findRegions(getAnimationNameForForceFieldType(fft, false, true)));
                if(fft == ForceFieldType.LASER){
                    leftBreakAni.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
                    rightBreakAni.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
                }
                String name = getFlourishAnimationNameForForceFieldType(fft);
                Animation flourishAni = null;
                if(name != null){
                    flourishAni = new Animation(WALL_BREAK_CYCLE_RATE, aniAtlas.findRegions(getFlourishAnimationNameForForceFieldType(fft)));
                }
                Wall wall = new Wall(leftWall, rightWall, collider, fft);
                wall.addFlourishing(flourish);
                wall.addStateAnimation("BREAKING", leftBreakAni, rightBreakAni, flourishAni);
                wall.setXVelocity(wallAndFloorVelocity);

                walls.add(wall);

                //Add The Indicator
                TextureRegion region = fft == ForceFieldType.LASER ? spriteAtlas.findRegion(AtlasUtil.SPRITE_RED_INDICATOR) :
                        fft == ForceFieldType.PLASMA ? spriteAtlas.findRegion(AtlasUtil.SPRITE_GREEN_INDICATOR) :
                                spriteAtlas.findRegion(AtlasUtil.SPRITE_BLUE_INDICATOR);
                Indicator indicator = new Indicator(xPos, yPos, 75f, 75f, region, fft);
                indicator.velocity.x = indicatorSpeed;
                indicator.setDisposer(indicatorDisposer);
                rightWallGroup.addActor(indicator);
                indicators.add(indicator);
                xPos += indicator.getWidth() + 10f;
            }

            nextGeneration += millisBetweenWalls;
        }
    }

    private Indicator getFirstIndicatorNotCovered(){
        Indicator i = null;
        for(Indicator indicator:indicators){
            if(!indicator.isCovered()){
                i = indicator;
                break;
            }
        }
        return i;
    }

    private float getAnimationCycleRateForForceFieldType(ForceFieldType fft, boolean isBreaking){
        float rate = WALL_CYCLE_RATE;
        if(isBreaking){
            switch(fft){
                case LIGHTNING:
                    rate = TESLA_CYCLE_RATE;
                    break;
                case LASER:
                    rate = LASER_CYCLE_RATE;
                    break;
                default:
                    rate = WALL_BREAK_CYCLE_RATE;
                    break;
            }
            return rate;
        }

        switch(fft){
            case LIGHTNING:
                rate = TESLA_CYCLE_RATE;
                break;
            case LASER:
                rate = LASER_CYCLE_RATE;
                break;
            default:
                break;
        }
        return rate;
    }

    private String getFlourishAnimationNameForForceFieldType(ForceFieldType fft){
        String name = null;
        switch(fft){
            case LIGHTNING:
                //name = AtlasUtil.ANI_TESLA_FLOURISH;
                break;
            case PLASMA:
                name = AtlasUtil.ANI_PLASMA_FLOURISH;
                break;
            case LASER:
                //name = AtlasUtil.ANI_LASER_FLOURISH;
                break;
            default:
                break;
        }

        return name;
    }

    private String getAnimationNameForForceFieldType(ForceFieldType fft, boolean isLeft, boolean isBreaking){
        String name = null;
        switch(fft){
            case LIGHTNING:
                if(isLeft){
                    name = isBreaking ? AtlasUtil.ANI_WALL_TESLA_BR_L : AtlasUtil.ANI_WALL_TESLA_L;
                }else{
                    name = isBreaking ? AtlasUtil.ANI_WALL_TESLA_BR_R : AtlasUtil.ANI_WALL_TESLA_R;
                }
                break;
            case PLASMA:
                if(isLeft){
                    name = isBreaking ? AtlasUtil.ANI_WALL_PLASMA_BR_L : AtlasUtil.ANI_WALL_PLASMA_L;
                }else{
                    name = isBreaking ? AtlasUtil.ANI_WALL_PLASMA_BR_R : AtlasUtil.ANI_WALL_PLASMA_R;
                }
                break;
            case LASER:
                if(isLeft){
                    name = isBreaking ? AtlasUtil.ANI_WALL_LASER_BR_L : AtlasUtil.ANI_WALL_LASER_L;
                }else{
                    name = isBreaking ? AtlasUtil.ANI_WALL_LASER_BR_R : AtlasUtil.ANI_WALL_LASER_R;
                }
                break;
        }

        return name;
    }

    private WallPattern getRandomWallPattern(){
        WallPattern p = new WallPattern(20f);
        Random r = new Random(System.currentTimeMillis());
        //Based on the Max Fields, we generated a new pattern
        int numFields = r.nextInt(MAX_FIELDS) + 1;
        if(numFields < MIN_FIELDS){
            numFields = MIN_FIELDS;
        }
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

        comboDecorator.setShakeSpeed(result);
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
            currentComboLevel = ComboLevels.PERFECT;
            wasChanged = true;
        }else if(latestCombo == COMBO_THRESHOLDS[4]){
            currentComboLevel = ComboLevels.UNBELIEVABLE;
            wasChanged = true;
        }else if(latestCombo == COMBO_THRESHOLDS[5]){
            currentComboLevel = ComboLevels.AMAZING;
            wasChanged = true;
        }else if(latestCombo == COMBO_THRESHOLDS[6]){
            currentComboLevel = ComboLevels.UNSTOPPABLE;
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
        if(puke.isVisible()){
            puke.setVisible(false);
        }

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
            switch(w.forceFieldType){
                case PLASMA:
                    dieGlassSound.play(sfxVolume);
                    break;
                case LASER:
                    dieFireSound.play(sfxVolume);
                    if(AnimationUtil.CHARACTER_1.equals(characterSelected)){
                        dieEdisonSound.play(sfxVolume/2f);
                    }else{
                        dieEdynSound.play(sfxVolume/2f);
                    }
                    break;
                case LIGHTNING:
                    dieShockSound.play(sfxVolume);
                    if(AnimationUtil.CHARACTER_1.equals(characterSelected)){
                        dieEdisonSound.play(sfxVolume/2f);
                    }else{
                        dieEdynSound.play(sfxVolume/2f);
                    }
                    break;
            }

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
            initializeVolumes();
            String charSelect = gameProcessor.getStoredString(GameOptions.CHARACTER_SELECT_KEY);
            if(!"".equals(charSelect) && !charSelect.equals(characterSelected)){
                characterSelected = charSelect;
                if(cinematicComplete && player != null){
                    setPlayerAnimations();
                }
            }

            player.setState(PlayerStates.DEFAULT);
            player.setIsDead(false);
            deathOverlay.setVisible(false);
            info.reset();
            MAX_FIELDS = DEFAULT_MAX_FIELDS;
            MIN_FIELDS = DEFAULT_MIN_FIELDS;

            currentComboLevel = ComboLevels.NONE;
            currentCombo = 0;
            highestRunCombo = 0;

            for(Wall w: walls){
                w.setIsRemovable(true);
            }
            for(Indicator i:indicators){
                i.setIsRemovable(true);
            }
            shields.clearFields();
            millisBetweenWalls = BASE_TIME_BETWEEN_WALLS;
            isDead = false;

            //  shields.maxFields = info.maxFields;

            setEnvVelocity(wallAndFloorVelocity);

            bodyLimit.restoreAllResourceLevels();
            if(cinematicComplete){
                music.play();
            }
        }
    }

    private void setEnvVelocity(float velocity){
        for(Actor a:floorGroup.getChildren()){
            ((GenericActor)a).velocity.x = velocity;
        }

        for(Actor a:floorAccentGroup.getChildren()){
            ((GenericActor)a).velocity.x = velocity;
        }

        for(Actor a:tunnelGroup.getChildren()){
            ((GenericActor)a).velocity.x = velocity;
        }
    }

    private void incrementMaxFields(){
        MAX_FIELDS += 1;
        if(MAX_FIELDS > 3 && MAX_FIELDS %2 == 0){
            MIN_FIELDS += 1;
        }
    }

    private void addField(ForceFieldType fft){
        if(isDead){
            return;
        }

        boolean wasAdded = false;
        int resLevel = bodyLimit.getResourceLevel();
        boolean isPuking = puke.isVisible() && !puke.isAnimationComplete();
        if(isPuking){
            return;
        }

        if((bodyLimit.getHeatMax() - resLevel) >= shields.resourceUsage){
            //Yuck, I don't like this, but I can't come up with an
            //  argument for not doing this. The Stage manages the
            //  state and performs the corresponding actions.
            shields.addField(fft);
            bodyLimit.incrementHeat(shields.resourceUsage);
            wasAdded = true;
        }
        if(wasAdded){
            Indicator firstIndicator = getFirstIndicatorNotCovered();
            if(firstIndicator != null && firstIndicator.forceFieldType == fft){
                firstIndicator.setCovered(true);
            }
            shieldUpSound.play(sfxVolume);
            player.startShield();
        }else{
            if(characterSelected.equals(AnimationUtil.CHARACTER_2)){
                edynSickSound.play(sfxVolume);
            }else{
                edisonSickSound.play(sfxVolume);
            }
            player.startPuking();
            puke.restart();
            puke.setVisible(true);
        }

    }

    private void addLightningField(){
        addField(ForceFieldType.LIGHTNING);
        leftControls.setPressed(ForceFieldType.LIGHTNING);
        rightControls.setPressed(ForceFieldType.LIGHTNING);
    }

    private void addPlasmaField(){
        addField(ForceFieldType.PLASMA);
        leftControls.setPressed(ForceFieldType.PLASMA);
        rightControls.setPressed(ForceFieldType.PLASMA);
    }

    private void addLaserField(){
        addField(ForceFieldType.LASER);
        leftControls.setPressed(ForceFieldType.LASER);
        rightControls.setPressed(ForceFieldType.LASER);
    }


    public void regenResources(int increment){
        bodyLimit.incrementHeat(-increment);
    }

////
///INITIALIZERS
//--------------
    private void initializeVolumes(){
        bgVolume = gameProcessor.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY);
        if(bgVolume < 0){
            bgVolume = 0f;
        }
        sfxVolume = gameProcessor.getStoredFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY);
        if(sfxVolume < 0){
            sfxVolume = 0f;
        }
    }

    private void initializeAmbience() {

        initializeVolumes();
        music = assetManager.get(AssetsUtil.GAME_BG_MUSIC, AssetsUtil.MUSIC);
        music.setVolume(bgVolume);
        music.setLooping(true);
        this.gameProcessor.setBGMusic(music);

        badShieldSound = assetManager.get(AssetsUtil.SND_SHIELD_WRONG, AssetsUtil.SOUND);
        edisonSickSound = assetManager.get(AssetsUtil.SND_SICK_EDISON, AssetsUtil.SOUND);
        edynSickSound = assetManager.get(AssetsUtil.SND_SICK_EDYN, AssetsUtil.SOUND);
        shieldUpSound = assetManager.get(AssetsUtil.SND_SHIELD_ON, AssetsUtil.SOUND);
        dieGlassSound = assetManager.get(AssetsUtil.SND_DEATH_THUD, AssetsUtil.SOUND);
        dieShockSound = assetManager.get(AssetsUtil.SND_DEATH_SHOCK, AssetsUtil.SOUND);
        dieFireSound = assetManager.get(AssetsUtil.SND_DEATH_FIRE, AssetsUtil.SOUND);
        dieEdisonSound = assetManager.get(AssetsUtil.SND_DEATH_EDISON, AssetsUtil.SOUND);
        dieEdynSound = assetManager.get(AssetsUtil.SND_DEATH_EDYN, AssetsUtil.SOUND);
        breakElectricSound = assetManager.get(AssetsUtil.SND_WB_ELECTRIC, AssetsUtil.SOUND);
        breakGlassSound = assetManager.get(AssetsUtil.SND_WB_GLASS, AssetsUtil.SOUND);
        breakLaserSound = assetManager.get(AssetsUtil.SND_WB_LASER, AssetsUtil.SOUND);
    }

    private void initializeOverlays() {
        BitmapFont mainFont = assetManager.get(AssetsUtil.NEUROPOL_64, AssetsUtil.BITMAP_FONT);
        BitmapFont subFont = assetManager.get(AssetsUtil.NEUROPOL_32, AssetsUtil.BITMAP_FONT);

        alarmOverlay = new Overlay(0, 0, getWidth(), getHeight(), Color.RED, Color.RED, mainFont, subFont, "", "");
        alarmOverlay.addDecorator(new ActorDecorator() {

            private float max = 0.15f;
            private float min = 0.0f;
            private float current = 0.0f;
            private float flutterSpeed = 0.075f;

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
        deathOverlay = new Overlay(0, 0, getWidth(), getHeight(), Color.PURPLE, Color.WHITE, mainFont, subFont, "You Failed to Escape!", subText, 0.4f);

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
            String sickAniName = AnimationUtil.getPlayerSickAnimationName(characterSelected);
            String electroName = AnimationUtil.getPlayerElectroAnimationName(characterSelected);
            String wallName = AnimationUtil.getPlayerWallAnimationName(characterSelected);
            String fireName = AnimationUtil.getPlayerFireAnimationName(characterSelected);

            Animation defaultPlayerAnimation = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, aniAtlas.findRegions(aniName));
            player.resetAnimation(defaultPlayerAnimation);
            player.addStateAnimation(PlayerStates.DEFAULT, defaultPlayerAnimation);

            Animation shieldingAnimation = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, aniAtlas.findRegions(shieldAniName));
            player.setShieldingAnimation(shieldingAnimation);
            Animation sickPlayerAnimation = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, aniAtlas.findRegions(sickAniName));
            player.setPukingAnimation(sickPlayerAnimation);

            Animation electroAni = new Animation(AnimationUtil.RUNNER_ELECTRO_CYCLE_RATE, aniAtlas.findRegions(electroName));
            player.addStateAnimation(PlayerStates.ELECTRO_DEATH, electroAni);
            Animation fireAni = new Animation(AnimationUtil.RUNNER_FIRE_CYCLE_RATE, aniAtlas.findRegions(fireName));
            player.addStateAnimation(PlayerStates.FIRE_DEATH, fireAni);
            Animation wallAni = new Animation(AnimationUtil.RUNNER_WALL_CYCLE_RATE, aniAtlas.findRegions(wallName));
            player.addStateAnimation(PlayerStates.WALL_DEATH, wallAni);

            float px = characterSelected.equals(AnimationUtil.CHARACTER_2) ? PUKE_F_X : PUKE_M_X;
            float py = characterSelected.equals(AnimationUtil.CHARACTER_2) ? PUKE_F_Y : PUKE_M_Y;
            Animation ani = new Animation(AnimationUtil.RUNNER_CYCLE_RATE, aniAtlas.findRegions(AtlasUtil.ANI_PUKE));
            puke = new AnimatedActor(px, py, PUKE_W, PUKE_H, ani, 0f);
            addActor(puke);
            puke.flipTextureRegion(true, false);
            puke.setIsLooping(false);
            puke.setVisible(false);
            puke.pause();
        }
    }
    private void initializePlayer() {

        player = new Player(playerDimensions[0],
                playerDimensions[1],
                playerDimensions[2],
                playerDimensions[3],
                null);
        setPlayerAnimations();
        addActor(player);

        //player.setPukingFlourish(puke);

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

    private TextureRegion getAccentTextureRegion(){

        Array<TextureAtlas.AtlasRegion> hazards = spriteAtlas.findRegions(AtlasUtil.SPRITE_HAZARD);
        int segment = rand.nextInt(100);
        int index = 0;
        if(segment < 50){
            index = 1;
        }
        return hazards.get(index);
    }

    public void generateNextFloor(){
        int currentFloorCount = floorGroup.getChildren().size;
        float nextPos = currentFloorCount == 0 ? 0f : floorGroup.getChildren().get(currentFloorCount-1).getRight();
        GenericActor floor = new GenericActor(nextPos + FLOOR_X_OFFSET, FLOOR_Y, FLOOR_WIDTH, FLOOR_HEIGHT, getFloorTextureRegion(), Color.GRAY);
        floor.velocity.x = wallAndFloorVelocity;
        floor.addDecorator(offScreenDecorator);
        floorGroup.addActor(floor);
    }

    public void generteNextFloorAccent(){
        int currentAccentCount = floorAccentGroup.getChildren().size;
        float nextPos = currentAccentCount == 0 ? 0f : floorAccentGroup.getChildren().get(currentAccentCount - 1).getRight();
        GenericActor accent = new GenericActor(nextPos, ACCENT_Y, ACCENT_WIDTH, ACCENT_HEIGHT, getAccentTextureRegion(), Color.GRAY);
        accent.velocity.x = wallAndFloorVelocity;
        accent.addDecorator(offScreenDecorator);
        accent.flipTextureRegion(false, true);
        floorAccentGroup.addActor(accent);
        GenericActor accentTwo = new GenericActor(nextPos, FLOOR_HEIGHT-ACCENT_HEIGHT, ACCENT_WIDTH, ACCENT_HEIGHT, getAccentTextureRegion(), Color.GRAY);
        accentTwo.velocity.x = wallAndFloorVelocity;
        accentTwo.addDecorator(offScreenDecorator);
        floorAccentGroup.addActor(accentTwo);
    }

    public TunnelAnimation buildAnimationForTunnel(){
        int segment = rand.nextInt(1000);
        if(tunnelGroupHasSpecialTunnel){
            segment += 150; //skip the specials, and increase Basic chances
        }
        TunnelAnimation animation;
        if(segment < 5){
            //nessie
            animation = new TunnelAnimation(new Animation(1f/3f, aniAtlas.findRegions(AtlasUtil.ANI_NESSIE_WALL)), true, false);
            tunnelGroupHasSpecialTunnel = true;
        }else if(segment < 10){
            //Sassie
            animation = new TunnelAnimation(new Animation(1f/6f, aniAtlas.findRegions(AtlasUtil.ANI_SASSIE_WALL)), true, true);
            animation.animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
            tunnelGroupHasSpecialTunnel = true;
        }else if(segment < 80){
            //baldGuy
            animation = new TunnelAnimation(new Animation(1f, aniAtlas.findRegions(AtlasUtil.ANI_GUY_WALL)), true);
            animation.animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
            tunnelGroupHasSpecialTunnel = true;
        }else if(segment < 150){
            //labLady
            animation = new TunnelAnimation(new Animation(1f, aniAtlas.findRegions(AtlasUtil.ANI_LADY_WALL)), true);
            animation.animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
            tunnelGroupHasSpecialTunnel = true;
        }else if(segment < 450){
            //cracked
            animation = new TunnelAnimation(new Animation(1f, aniAtlas.findRegions(AtlasUtil.ANI_CRACKED_WALL)), false);
        }else if(segment < 750){
            //stained
            animation = new TunnelAnimation(new Animation(1f, aniAtlas.findRegions(AtlasUtil.ANI_STAINED_WALL)), false);
        }else{
            //basic
            animation = new TunnelAnimation(new Animation(1f, aniAtlas.findRegions(AtlasUtil.ANI_BASIC_WALL)), false);
        }

        return animation;
    }

    public void generateNextTunnel(){
        int currentTunnelCount = tunnelGroup.getChildren().size;
        float nextPos = currentTunnelCount == 0 ? 0f : tunnelGroup.getChildren().get(currentTunnelCount-1).getRight();
        TunnelAnimation ani = buildAnimationForTunnel();
        TunnelSegment tunnel = new TunnelSegment(nextPos, TNL_Y, TNL_WIDTH, TNL_HEIGHT, ani.animation, 0f);
        tunnel.setSpecial(ani.isSpecial);
        tunnel.velocity.x = wallAndFloorVelocity;
        tunnel.addDecorator(offScreenDecorator);
        tunnel.setIsLooping(ani.shouldLoop);
        tunnel.pause();
        tunnel.addDecorator(resumeAnimationOnScreenDecorator);
        tunnelGroup.addActor(tunnel);
    }
    
    private void initializeStartingScene(){
        for(int i=0;i<floorCount;i++){
            generateNextFloor();
        }

        for(int i=0;i<accentCount;i++){
            generteNextFloorAccent();
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
        info = new GameInfo(infoX, infoY, infoWidth, infoHeight, assetManager.get(AssetsUtil.NEUROPOL_32, AssetsUtil.BITMAP_FONT));
        addActor(info);
    }

    @Override
    public boolean keyDown(int keyCode) {

        if(!cinematicComplete){
            cinematic.stop();
            return false;
        }
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
            cinematic.stop();
            cinematic.setVisible(false);
        }
    }

    private void initializeButtonControls(){

        // BLUE



        if(true || Gdx.app.getType() != Application.ApplicationType.Desktop){

            EventListener blistener = new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    addLightningField();
                }
            };
            EventListener glistener = new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    addPlasmaField();
                }
            };
            EventListener rlistener = new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    addLaserField();
                }
            };

            Animation blueDefAni = new Animation(1f, aniAtlas.findRegions(AtlasUtil.ANI_BUTTON_BLUE_DEF));
            Animation bluePressedAni = new Animation(0.5f/3f, aniAtlas.findRegions(AtlasUtil.ANI_BUTTON_BLUE_PRESSED));
            Animation greenDefAni = new Animation(1f, aniAtlas.findRegions(AtlasUtil.ANI_BUTTON_GREEN_DEF));
            Animation greenPressedAni = new Animation(0.5f/3f, aniAtlas.findRegions(AtlasUtil.ANI_BUTTON_GREEN_PRESSED));
            Animation redDefAni = new Animation(1f, aniAtlas.findRegions(AtlasUtil.ANI_BUTTON_RED_DEF));
            Animation redPressedAni = new Animation(0.5f/3f, aniAtlas.findRegions(AtlasUtil.ANI_BUTTON_RED_PRESSED));

            float leftX, rightX, y, width, height;
            leftX = 0f;
            rightX = getWidth() - 240f;
            y = 80f;
            width = 100f;
            height = getHeight();
            leftControls = new ControlGroup(leftX, y, width, height, Color.CYAN);
            rightControls = new ControlGroup(rightX, y, width, height, Color.CYAN);

            float rotation = 0f;

            leftControls.addButton(ForceFieldType.LIGHTNING, blistener, blueDefAni, bluePressedAni, rotation, false);
            leftControls.addButton(ForceFieldType.PLASMA, glistener, greenDefAni, greenPressedAni, rotation, false);
            leftControls.addButton(ForceFieldType.LASER, rlistener, redDefAni, redPressedAni, rotation, false);

            // Right Side
            Animation rblueDefAni = new Animation(1f, aniAtlas.findRegions(AtlasUtil.ANI_BUTTON_BLUE_DEF));
            Animation rbluePressedAni = new Animation(0.5f/3f, aniAtlas.findRegions(AtlasUtil.ANI_BUTTON_BLUE_PRESSED));
            Animation rgreenDefAni = new Animation(1f, aniAtlas.findRegions(AtlasUtil.ANI_BUTTON_GREEN_DEF));
            Animation rgreenPressedAni = new Animation(0.5f/3f, aniAtlas.findRegions(AtlasUtil.ANI_BUTTON_GREEN_PRESSED));
            Animation rredDefAni = new Animation(1f, aniAtlas.findRegions(AtlasUtil.ANI_BUTTON_RED_DEF));
            Animation rredPressedAni = new Animation(0.5f/3f, aniAtlas.findRegions(AtlasUtil.ANI_BUTTON_RED_PRESSED));
            rightControls.addButton(ForceFieldType.LIGHTNING, blistener, rblueDefAni, rbluePressedAni, rotation, true);
            rightControls.addButton(ForceFieldType.PLASMA, glistener, rgreenDefAni, rgreenPressedAni, rotation, true);
            rightControls.addButton(ForceFieldType.LASER, rlistener, rredDefAni, rredPressedAni, rotation, true);
        }

        addActor(leftControls);
        addActor(rightControls);
    }

    public void resume(){
        initializeVolumes();
        cinematicComplete = false;
        resetGame();
        cinematic.setVisible(true);
        cinematic.start();

    }

}
