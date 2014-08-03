package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
import com.kasetagen.engine.gdx.scenes.scene2d.KasetagenStateUtil;
import com.kasetagen.game.bubblerunner.data.GameOptions;
import com.kasetagen.game.bubblerunner.data.GameStats;
import com.kasetagen.game.bubblerunner.data.IDataSaver;
import com.kasetagen.game.bubblerunner.data.WallPattern;
import com.kasetagen.game.bubblerunner.delegate.IGameProcessor;
import com.kasetagen.game.bubblerunner.scene2d.actor.*;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class BubbleRunnerStage extends Stage {


    private static final float HUD_HEIGHT = 40f;
    private static final float FLOOR_HEIGHT = 160f;

    private static final int SECONDS_BETWEEN_DIFF_SHIFT = 30;
    private static final long BASE_TIME_BETWEEN_WALLS = 4000L;
    private static final int SECONDS_BETWEEN_ADJUSTS = 5;
    private static final float TIME_DECREASE = 100f;
    private static final float MIN_TIME_BETWEEN_WALLS = 300f;

    private static final int SECONDS_PER_RESOURCE_REGEN = 2;


    //Order of values:  xPos, yPos, width, height
    private static final float INDICATOR_WIDTH = Gdx.graphics.getWidth()/4;
    private static final float INDICATOR_HEIGHT = Gdx.graphics.getHeight()/4;

    private static float[] playerDimensions = new float[] { 100f, FLOOR_HEIGHT, Gdx.graphics.getWidth()/4, (Gdx.graphics.getHeight()/3)*2 }; //old width 160f
    private static float[] floorDimensions = new float[] { 0f, 0f, Gdx.graphics.getWidth(), FLOOR_HEIGHT };
    private static float[] wallDimensions = new float[] {Gdx.graphics.getWidth()+FLOOR_HEIGHT,
                                                         FLOOR_HEIGHT, 40f, Gdx.graphics.getHeight()-FLOOR_HEIGHT };
    private static float[] warningIndicatorDimensions = new float[] {Gdx.graphics.getWidth()/2 - (INDICATOR_WIDTH/2), Gdx.graphics.getHeight()/2-(INDICATOR_HEIGHT/2),
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
    private float wallVelocity;

    private float secondsSinceResourceRegen = 0f;

    //Input Listeners
    private InputListener createAndLeaveListener;
    private InputListener keysReleasedListener;
    private InputListener currentListener;

    //Actors
    public Player player;
    public Actor floor;
    public Array<Wall> walls;
    public Wall collidedWall = null;
    public GameInfo info;
    public Overlay deathOverlay;
    public ControlGroup controls;

    //Ambience (Music and Effects
    private WarningIndicator warningIndicator;
    private Music music;
    private Sound zapSound;
    private Sound powerOnSound;
    private Sound explosionSound;
    //TODO: Move into Player?
    private ParticleEffect particleBubble;

    private float bgVolume;
    private float sfxVolume;


    public BubbleRunnerStage(IGameProcessor gameProcessor){
        this.gameProcessor = gameProcessor;
    	assetManager = this.gameProcessor.getAssetManager();
    	batch = this.getBatch();

        //Initialize Privates
        wallsToRemove = new Array<Wall>();

        highScore = gameProcessor.getStoredInt(GameStats.HIGH_SCORE_KEY);
        mostMisses = gameProcessor.getStoredInt(GameStats.MOST_MISSES_KEY);

        //SET WALL VELOCITY
        wallVelocity = -1f*(getWidth()/2);
        //Add Player
        initializePlayer(GameInfo.DEFAULT_MAX_FIELDS);

        //Add Floor
        initializeFloor();

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

        }
        particleBubble.update(delta);
		particleBubble.setPosition(player.getX() + player.getWidth()/2, player.getY() + player.getHeight() / 4);
        //Update GameStats
    }

    @Override
    public void draw() {     
        batch.begin();
        //particleBubble.draw(batch);
        batch.end();
        super.draw();
    }

    private void processResources(){
        float secondsBetweenResourceRegen = (millisBetweenWalls/1000)/2;
        if(secondsSinceResourceRegen >= secondsBetweenResourceRegen){//SECONDS_PER_RESOURCE_REGEN){
            regenResources(1);
            //We want to keep any "left-over" time so that we don't get weird timing differences
            secondsSinceResourceRegen = secondsSinceResourceRegen - secondsBetweenResourceRegen;//SECONDS_PER_RESOURCE_REGEN;
        }
    }

    private void processObstacleMovements(float delta){
        for(Wall w:walls){
            w.setX(w.getX() + (w.velocity.x * delta));
        }
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
            boolean addAfter = warningIndicator == null;
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
                w.setXVelocity(wallVelocity);
                walls.add(w);
                addActor(w);
                w.setZIndex(0);
            }

            nextGeneration += millisBetweenWalls;
        }
    }

    private String getAnimationNameForForceFieldType(ForceFieldType fft){
        String name;
        switch(fft){
            case LIGHTNING:
                name = "walls/light-wall";
                break;
            case PLASMA:
                name = "walls/plasma-wall";
                break;
            case LASER:
                name = "walls/discharge-wall";
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
    private void initializeAmbience() {
        bgVolume = gameProcessor.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY);
        music = assetManager.get(AssetsUtil.ALT_BG_MUSIC, AssetsUtil.MUSIC);
        music.setVolume(bgVolume);
        music.play();

        sfxVolume = gameProcessor.getStoredFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY);
        zapSound = assetManager.get(AssetsUtil.ZAP_SOUND, AssetsUtil.SOUND);
        powerOnSound = assetManager.get(AssetsUtil.POWER_ON_SOUND, AssetsUtil.SOUND);
        explosionSound = assetManager.get(AssetsUtil.EXPLOSION_SOUND, AssetsUtil.SOUND);
    }

    private void initializeDeathOverlay() {
        BitmapFont mainFont = assetManager.get(AssetsUtil.COURIER_FONT_32, AssetsUtil.BITMAP_FONT);
        BitmapFont subFont = assetManager.get(AssetsUtil.COURIER_FONT_18, AssetsUtil.BITMAP_FONT);
        deathOverlay = new Overlay(0, 0, getWidth(), getHeight(), Color.PURPLE, Color.WHITE, mainFont, subFont, "You Failed to Escape!", "Score: 0\nBest Score: 0");
        deathOverlay.setVisible(false);
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

    private void initializeFloor() {
        floor = new Floor(floorDimensions[0],
                floorDimensions[1],
                floorDimensions[2],
                floorDimensions[3]);
        addActor(floor);
    }

    private void initializeHUD() {
        float infoX = 0f;
        float infoY = Gdx.graphics.getHeight() - HUD_HEIGHT;
        float infoWidth = getWidth();
        float infoHeight = HUD_HEIGHT;
        info = new GameInfo(infoX, infoY, infoWidth, infoHeight, assetManager.get(AssetsUtil.COURIER_FONT_32, AssetsUtil.BITMAP_FONT), controls);
        addActor(info);
    }


    private void initializeInputListeners() {
        createAndLeaveListener = new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(Input.Keys.A == keycode){
                    addLightningField();
                }else if(Input.Keys.S == keycode){
                    addPlasmaField();
                }else if(Input.Keys.D == keycode){
                    addLaserField();
                }else if(Input.Keys.TAB == keycode){
                    //toggleListener();
                    KasetagenStateUtil.setDebugMode(!KasetagenStateUtil.isDebugMode());
                }else if(Input.Keys.SPACE == keycode){
                    resetGame();
                }
                return super.keyDown(event, keycode);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(isDead){
                    Actor a = hit(x, y, true);
                    if(a != null && a instanceof Overlay){
                        resetGame();
                    }
                }

                return super.touchDown(event, x, y, pointer, button);
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

        addActor(controls);
    }



}
