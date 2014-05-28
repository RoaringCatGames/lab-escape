package com.kasetagen.game.bubblerunner.scene2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.kasetagen.game.bubblerunner.data.GameStats;
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

    public Player player;
    public Actor floor;
    public Array<Wall> walls;

    public GameStats stats;


    public BubbleRunnerStage(){

        //Add Floor
        floor = new Actor();
        floor.setPosition(0f, 0f);
        floor.setWidth(getWidth());
        floor.setHeight(20f);
        floor.setOrigin(floor.getWidth() / 2, floor.getHeight() / 2);
        floor.setBounds(0f, 0f, floor.getWidth(), floor.getHeight());
        floor.setColor(Color.BLACK);
        addActor(floor);

        //Add Player
        player = new Player();
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
