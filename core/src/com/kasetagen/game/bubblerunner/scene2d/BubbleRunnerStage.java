package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.kasetagen.game.bubblerunner.data.GameStats;
import com.kasetagen.game.bubblerunner.scene2d.actor.Floor;
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

    private Array<Wall> wallsToRemove;
    private long lastWallTime = 0L;
    private long nextGeneration = 1000L;
    private float timePassed = 0f;
    private long timeBetweenWalls = 2000L;
    private float wallAdjustment = 10f;

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

        //Initialize Walls
        walls = new Array<Wall>();

        //Initialize Stats
        stats = new GameStats();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        //Calculate timestep
        timePassed += delta*1000;

        //Update GameStats

        //Move Walls Closer based on Speed
        for(Wall w:walls){

            //Check for Collisions and apply player/wall information
            if(w.collider.overlaps(player.collider)){
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
                              wallDimensions[3]);
            walls.add(w);
            addActor(w);
            w.setZIndex(0);
            lastWallTime = System.currentTimeMillis();
            nextGeneration += timeBetweenWalls;
        }
    }

    @Override
    public void draw() {
        super.draw();

    }
}
