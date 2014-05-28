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

    private float[] playerDimensions = new float[] { 20f, 20f, 80f, 320f };
    private float[] floorDimensions = new float[] { 0f, 0f, Gdx.graphics.getWidth(), 20f };

    public Player player;
    public Actor floor;
    public Array<Wall> walls;

    public GameStats stats;

    public BubbleRunnerStage(){

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

        //Check for Collisions and apply player/wall information

        //Update GameStats

        //Move Walls Closer based on Speed

        //Add New Wall(s) based on time

    }

    @Override
    public void draw() {
        super.draw();

    }
}
