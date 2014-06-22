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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
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
    private static final float FLOOR_HEIGHT = 80f;

	private IGameProcessor gameProcessor;
	private AssetManager assetManager;
    private int highScore = 0;
    private boolean isDead = false;

    //Order of values:  xPos, yPos, width, height
    private float[] playerDimensions = new float[] { 200f, FLOOR_HEIGHT, 160f, Gdx.graphics.getHeight()/2 };
    private float[] floorDimensions = new float[] { 0f, 0f, Gdx.graphics.getWidth(), FLOOR_HEIGHT };
    private float[] wallDimensions = new float[] {Gdx.graphics.getWidth()-FLOOR_HEIGHT,
                                                  FLOOR_HEIGHT, 40f, Gdx.graphics.getHeight()-FLOOR_HEIGHT };
    
    private ForceFieldType[] wallTypes = new ForceFieldType[] { ForceFieldType.BUBBLE, ForceFieldType.ELECTRIC, ForceFieldType.ION };

    private Array<Wall> wallsToRemove;
    private long lastWallTime = 0L;
    private long nextGeneration = 1000L;
    private float timePassed = 0f;
    private long timeBetweenWalls = 2000L;
    private float wallAdjustment = 10f;
    
    private ParticleEffect particleBubble;
    
    private Batch batch;

    private InputListener createAndLeaveListener;
    private InputListener keysReleasedListener;
    private InputListener currentListener;

    public Player player;
    public Actor floor;
    public Array<Wall> walls;
    public Wall collidedWall = null;
    public GameInfo info;
    public Overlay deathOverlay;
    
    private Music music;
    
    
    public BubbleRunnerStage(IGameProcessor gameProcessor){
        this.gameProcessor = gameProcessor;
    	assetManager = this.gameProcessor.getAssetManager();
    	batch = this.getBatch();

        //Initialize Stats
        info = new GameInfo(0f, Gdx.graphics.getHeight() - HUD_HEIGHT, getWidth(), HUD_HEIGHT, assetManager.get(AssetsUtil.COURIER_FONT_32, AssetsUtil.BITMAP_FONT));

        //Initialize Privates
        wallsToRemove = new Array<Wall>();

        //Add Floor
        floor = new Floor(floorDimensions[0],
                          floorDimensions[1],
                          floorDimensions[2],
                          floorDimensions[3]);
        addActor(floor);

        //Add Player
        player = new Player(playerDimensions[0],
                            playerDimensions[1],
                            playerDimensions[2],
                            playerDimensions[3],
                            new TextureRegion(assetManager.get(AssetsUtil.PLAYER_IMG, AssetsUtil.TEXTURE)));
        player.maxFields = info.maxFields;
        addActor(player);

        addActor(info);

        //Initialize Walls
        walls = new Array<Wall>();

        initializeInputListeners();
        
        particleBubble = assetManager.get(AssetsUtil.BUBBLE_PARTICLE, AssetsUtil.PARTICLE);
        //particleBubble.load(Gdx.files.internal("particles/bubble.p"), Gdx.files.internal("data/images/particles"));	
        particleBubble.start();
        particleBubble.findEmitter("bubble1").setContinuous(true); // reset works for all emitters of particle
        music = Gdx.audio.newMusic(Gdx.files.internal(AssetsUtil.BACKGROUND_SOUND));
        music.play();

        BitmapFont mainFont = assetManager.get(AssetsUtil.COURIER_FONT_32, AssetsUtil.BITMAP_FONT);
        BitmapFont subFont = assetManager.get(AssetsUtil.COURIER_FONT_18, AssetsUtil.BITMAP_FONT);
        deathOverlay = new Overlay(0, 0, getWidth(), getHeight(), Color.DARK_GRAY, Color.BLUE, mainFont, subFont, "You Died!", "Score: 0\nBest Score: 0");
        deathOverlay.setVisible(false);
        addActor(deathOverlay);

        setupButtonControls();
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        if(isDead){

        }else{
            //Calculate timestep
            timePassed += delta*1000;

            //Move Walls Closer based on Speed
            ForceField outerField = player.getOuterForceField();
            for(Wall w:walls){

                //Check for Collisions and apply player/wall information
                if(outerField != null && w.collider.overlaps(outerField.collider)){

                    if(w.forceFieldType == outerField.forceFieldType){
                        wallsToRemove.add(w);
                        info.score += 1;
                    }

                    //Destroy the forcefield if it collides with a wall
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

            //Remove Old Walls for now
            //TODO: We'll want to remove them on player destruction
            //      of the walls.
            for(Wall w:wallsToRemove){
                walls.removeValue(w, true);
                w.remove();
            }
            wallsToRemove.clear();

            //Add New Wall(s) based on time
            generateWall();

        }
		particleBubble.update(delta);
		particleBubble.setPosition(player.getX() + player.getWidth()/2, player.getY() + player.getHeight() / 4);
        //Update GameStats
    }

    private void generateWall() {
        Random r = new Random(System.currentTimeMillis());
        if(timePassed >= nextGeneration){
            Wall w = new Wall(wallDimensions[0],
                              wallDimensions[1],
                              wallDimensions[2],
                              wallDimensions[3],
                              wallTypes[r.nextInt(3)]);
            walls.add(w);
            addActor(w);
            w.setZIndex(0);
            lastWallTime = System.currentTimeMillis();
            nextGeneration += timeBetweenWalls;
        }
    }

    @Override
    public void draw() {     
        batch.begin();
        particleBubble.draw(batch);
        batch.end();
        super.draw();
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

    private void addAField(){
        player.addField(ForceFieldType.BUBBLE);
    }

    private void addSField(){
        player.addField(ForceFieldType.ELECTRIC);
    }

    private void addDField(){
        player.addField(ForceFieldType.ION);
    }

    private void initializeInputListeners() {
        createAndLeaveListener = new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(Input.Keys.A == keycode){
                    addAField();
                }else if(Input.Keys.S == keycode){
                    addSField();
                }else if(Input.Keys.D == keycode){
                    addDField();
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
                    player.addField(ForceFieldType.BUBBLE, 0);
                    isADown = true;
                }else if(Input.Keys.S == keycode && !isSDown){
                    player.addField(ForceFieldType.ELECTRIC, 0);
                    isSDown = true;
                }else if(Input.Keys.D == keycode & !isDDown){
                    player.addField(ForceFieldType.ION, 0);
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
                    player.removeField(ForceFieldType.BUBBLE);
                    isADown = false;
                }else if(Input.Keys.S == keycode && isSDown){
                    player.removeField(ForceFieldType.ELECTRIC);
                    isSDown = false;
                }else if(Input.Keys.D == keycode & isDDown){
                    player.removeField(ForceFieldType.ION);
                    isDDown = false;
                }
                return super.keyDown(event, keycode);
            }
        };

        this.addListener(createAndLeaveListener);
        currentListener = createAndLeaveListener;
    }


    private void setupButtonControls(){

        ControlGroup controls = new ControlGroup(0, 0, getWidth(), 60f, Color.CYAN);


        TextureRegionDrawable aUp, aDown, aChecked,
                sUp, sDown, sChecked,
                dUp, dDown, dChecked;

        aUp = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.A_UP, AssetsUtil.TEXTURE)));
        aDown = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.A_DOWN, AssetsUtil.TEXTURE)));
        aChecked = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.A_CHECKED, AssetsUtil.TEXTURE)));

        sUp = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.S_UP, AssetsUtil.TEXTURE)));
        sDown = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.S_DOWN, AssetsUtil.TEXTURE)));
        sChecked = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.S_CHECKED, AssetsUtil.TEXTURE)));

        dUp = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.D_UP, AssetsUtil.TEXTURE)));
        dDown = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.D_DOWN, AssetsUtil.TEXTURE)));
        dChecked = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetsUtil.D_CHECKED, AssetsUtil.TEXTURE)));

        controls.addButton(aUp, aDown, aChecked, new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                addAField();
            }
        }, true);

        controls.addButton(sUp, sDown, sChecked, new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                addSField();
            }
        }, true);

        controls.addButton(dUp, dDown, dChecked, new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                addDField();
            }
        }, true);

        addActor(controls);
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
            isDead = false;
            music.play();
        }
    }

}
