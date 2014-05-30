package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.kasetagen.game.bubblerunner.data.GameStats;
import com.kasetagen.game.bubblerunner.scene2d.actor.Floor;
import com.kasetagen.game.bubblerunner.scene2d.actor.ForceField;
import com.kasetagen.game.bubblerunner.scene2d.actor.Player;
import com.kasetagen.game.bubblerunner.scene2d.actor.Wall;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class BubbleRunnerStage extends Stage {

    //Order of values:  xPos, yPos, width, height
    private float[] playerDimensions = new float[] { 20f, 20f, 160f, Gdx.graphics.getHeight()/2 };
    private float[] floorDimensions = new float[] { 0f, 0f, Gdx.graphics.getWidth(), 20f };
    private float[] wallDimensions = new float[] {Gdx.graphics.getWidth()-20f,
                                                  20f, 40f, Gdx.graphics.getHeight()-20f };
    private ForceField[] wallTypes = new ForceField[] { ForceField.BUBBLE, ForceField.ELECTRIC, ForceField.ION };

    private Array<Wall> wallsToRemove;
    private long lastWallTime = 0L;
    private long nextGeneration = 1000L;
    private float timePassed = 0f;
    private long timeBetweenWalls = 2000L;
    private float wallAdjustment = 10f;

    private InputListener createAndLeaveListener;
    private InputListener keysReleasedListener;
    private InputListener currentListener;

    public Player player;
    public Actor floor;
    public Array<Wall> walls;


    public GameStats stats;

    public BubbleRunnerStage(){

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
                            playerDimensions[3]);
        addActor(player);
//        player.addField(ForceField.BUBBLE);
//        player.addField(ForceField.ELECTRIC);
//        player.addField(ForceField.ION);

        //Initialize Walls
        walls = new Array<Wall>();

        //Initialize Stats
        stats = new GameStats();

        createAndLeaveListener = new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(Input.Keys.A == keycode){
                    player.addField(ForceField.BUBBLE);
                }else if(Input.Keys.S == keycode){
                    player.addField(ForceField.ELECTRIC);
                }else if(Input.Keys.D == keycode){
                    player.addField(ForceField.ION);
                }else if(Input.Keys.TAB == keycode){
                    toggleListener();
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
                    player.addField(ForceField.BUBBLE, 0);
                    isADown = true;
                }else if(Input.Keys.S == keycode && !isSDown){
                    player.addField(ForceField.ELECTRIC, 0);
                    isSDown = true;
                }else if(Input.Keys.D == keycode & !isDDown){
                    player.addField(ForceField.ION, 0);
                    isDDown = true;
                }
                return super.keyDown(event, keycode);
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if(Input.Keys.A == keycode && isADown){
                    player.removeField(ForceField.BUBBLE);
                    isADown = false;
                }else if(Input.Keys.S == keycode && isSDown){
                    player.removeField(ForceField.ELECTRIC);
                    isSDown = false;
                }else if(Input.Keys.D == keycode & isDDown){
                    player.removeField(ForceField.ION);
                    isDDown = false;
                }
                return super.keyDown(event, keycode);
            }
        };

        this.addListener(createAndLeaveListener);
        currentListener = createAndLeaveListener;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        //Calculate timestep
        timePassed += delta*1000;

        //Move Walls Closer based on Speed
        for(Wall w:walls){
            //Check for Collisions and apply player/wall information
            if(player.collider.overlaps(w.collider)){
                Gdx.app.log("RUNNER GAME", "Player collided!");
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
            w.remove();
        }
        wallsToRemove.clear();

        //Add New Wall(s) based on time
        if(timePassed >= nextGeneration){
            Wall w = new Wall(wallDimensions[0],
                              wallDimensions[1],
                              wallDimensions[2],
                              wallDimensions[3],
                              wallTypes[walls.size % 3]);
            walls.add(w);
            addActor(w);
            w.setZIndex(0);
            lastWallTime = System.currentTimeMillis();
            nextGeneration += timeBetweenWalls;
        }

        //Update GameStats
    }

    @Override
    public void draw() {
        super.draw();
    }


    public void toggleListener(){

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
}
