package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
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
    private static final long BASE_TIME_BETWEEN_WALLS = 2000L;
    private static final int SECONDS_BETWEEN_ADJUSTS = 5;
    private static final float TIME_DECREASE = 100f;
    private static final float MIN_TIME_BETWEEN_WALLS = 300f;


    //Order of values:  xPos, yPos, width, height
    private static float[] playerDimensions = new float[] { 200f, FLOOR_HEIGHT, 160f, Gdx.graphics.getHeight()/2 };
    private static float[] floorDimensions = new float[] { 0f, 0f, Gdx.graphics.getWidth(), FLOOR_HEIGHT };
    private static float[] wallDimensions = new float[] {Gdx.graphics.getWidth()-FLOOR_HEIGHT,
                                                         FLOOR_HEIGHT, 40f, Gdx.graphics.getHeight()-FLOOR_HEIGHT };

    private static ForceFieldType[] wallTypes = new ForceFieldType[] { ForceFieldType.LIGHTNING, ForceFieldType.PLASMA, ForceFieldType.LASER};

    //Delegates
	private IGameProcessor gameProcessor;
	private AssetManager assetManager;
    private Batch batch;

    //State Values
    private int highScore = 0;
    private boolean isDead = false;

    //Obstacle Generation Values
    /**
     * Time Passed is in Seconds
     */
    private float secondsPassed = 0f;
    private Array<Wall> wallsToRemove;
    private int lastWallAdjustTime = 0;
    private long nextGeneration = 1000L;
    private long millisBetweenWalls = BASE_TIME_BETWEEN_WALLS;
    private float wallAdjustment = 10f;

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

    //Ambience (Music and Effects
    private Music music;
    //TODO: Move into Player?
    private ParticleEffect particleBubble;


    public BubbleRunnerStage(IGameProcessor gameProcessor){
        this.gameProcessor = gameProcessor;
    	assetManager = this.gameProcessor.getAssetManager();
    	batch = this.getBatch();

        //Initialize HUD (Stats, and GameInfo)
        initializeHUD();

        //Initialize Privates
        wallsToRemove = new Array<Wall>();

        //Add Floor
        initializeFloor();

        //Add Player
        initializePlayer(info.maxFields);

        //Initialize Walls
        walls = new Array<Wall>();

        //Setup our InputListeners
        initializeInputListeners();
        
        //Setup Background Music
        initializeBackgroundMusic();

        //Setup Death Overlay
        initializeDeathOverlay();

        //Add and Wire-up Button Controls
        initializeButtonControls();
    }




    @Override
    public void act(float delta) {
        super.act(delta);

        if(isDead){

        }else{
            //Calculate timestep
            secondsPassed += delta*1000;

            //Move Walls Closer based on Speed
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
        particleBubble.draw(batch);
        batch.end();
        super.draw();
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
                }else{
                    //If we hit a bad wall, we reduce your score
                    //  This will discourage jamming out fields like crazy
                    info.score -= 1;
                }

                //Destroy the forcefield if it collides with a wall
                //  The point and wall descrution is calculated above
                player.removeField(outerField);

            }else if(player.collider.overlaps(w.collider)){
                processDeath(w);
            }

            if(w.getX() <= (0f-w.getWidth()/2)){
                wallsToRemove.add(w);
            }else{
                w.setX(w.getX() - wallAdjustment);
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
        if(secondsPassed >= nextGeneration){
            WallPattern wp = getRandomWallPattern();
            for(int i=0;i<wp.wallCount;i++){
                //FORMULA:  xPos = startX + (N * (wallWidth + wallPadding)
                //          - Where N = NumberOfWalls-1
                Wall w = new Wall(wallDimensions[0] + (i*(wallDimensions[2] + wp.wallPadding)),
                        wallDimensions[1],
                        wallDimensions[2],
                        wallDimensions[3],
                        wp.forceFields.get(i));
                walls.add(w);
                addActor(w);
                w.setZIndex(0);
            }

            nextGeneration += millisBetweenWalls;
        }
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

    private void adjustDifficulty(){
        int secondsPassedInt  = (int)Math.floor(secondsPassed /1000);
        if(secondsPassedInt > 0 && secondsPassedInt != lastWallAdjustTime){
            if(secondsPassedInt%SECONDS_BETWEEN_ADJUSTS == 0){
                if(millisBetweenWalls > MIN_TIME_BETWEEN_WALLS){
                    Gdx.app.log("RUNNER", "Wall Decreasing");
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

        if(!w.equals(collidedWall)){
            wallsToRemove.add(w);
            assetManager.get(AssetsUtil.ZAP_SOUND, AssetsUtil.SOUND).play(1.0f);
            isDead = true;
            collidedWall = w;
            music.stop();
        }
        //Show Data
        //Wait for tap to restart
        if(info.score > highScore){
            highScore = info.score;
        }

        deathOverlay.setSubText("Score: " + info.score + "\nBest Score: " + highScore);
        deathOverlay.setVisible(true);
    }

    private void resetGame() {
        if(isDead){
            deathOverlay.setVisible(false);
            info.reset();
            for(Wall w: walls){
                w.remove();
            }
            walls.clear();
            player.clearFields();
            millisBetweenWalls = BASE_TIME_BETWEEN_WALLS;
            isDead = false;

            info.maxFields = 1;
            player.maxFields = 1;

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
        player.addField(fft);
    }

    private void addLightningField(){
        player.addField(ForceFieldType.LIGHTNING);
    }

    private void addPlasmaField(){
        player.addField(ForceFieldType.PLASMA);
    }

    private void addLaserField(){
        player.addField(ForceFieldType.LASER);
    }

////
///INITIALIZERS
//--------------
    private void initializeBackgroundMusic() {
        music = Gdx.audio.newMusic(Gdx.files.internal(AssetsUtil.BACKGROUND_SOUND));
        music.play();
    }

    private void initializeDeathOverlay() {
        BitmapFont mainFont = assetManager.get(AssetsUtil.COURIER_FONT_32, AssetsUtil.BITMAP_FONT);
        BitmapFont subFont = assetManager.get(AssetsUtil.COURIER_FONT_18, AssetsUtil.BITMAP_FONT);
        deathOverlay = new Overlay(0, 0, getWidth(), getHeight(), Color.DARK_GRAY, Color.BLUE, mainFont, subFont, "You Died!", "Score: 0\nBest Score: 0");
        deathOverlay.setVisible(false);
        addActor(deathOverlay);
    }

    private void initializePlayer(int maxFields) {
        player = new Player(playerDimensions[0],
                playerDimensions[1],
                playerDimensions[2],
                playerDimensions[3],
                new TextureRegion(assetManager.get(AssetsUtil.PLAYER_IMG, AssetsUtil.TEXTURE)));
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
        info = new GameInfo(infoX, infoY, infoWidth, infoHeight, assetManager.get(AssetsUtil.COURIER_FONT_32, AssetsUtil.BITMAP_FONT));
        //Start with single max fields
        info.maxFields = 1;
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
                    toggleListener();
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
                    toggleListener();
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

        ControlGroup controls = new ControlGroup(0, 0, getWidth(), 60f, Color.CYAN);


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
        }, true);

        controls.addButton(sUp, sDown, sChecked, new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                addPlasmaField();
            }
        }, true);

        controls.addButton(dUp, dDown, dChecked, new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                addLaserField();
            }
        }, true);

        addActor(controls);
    }



}
