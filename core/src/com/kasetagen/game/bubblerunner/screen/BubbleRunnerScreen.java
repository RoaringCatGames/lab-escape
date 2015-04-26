package com.kasetagen.game.bubblerunner.screen;

import com.kasetagen.engine.IGameProcessor;
import com.kasetagen.engine.screen.Kitten2dScreen;
import com.kasetagen.game.bubblerunner.scene2d.BubbleRunnerStage;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:55 PM
 */
public class BubbleRunnerScreen extends Kitten2dScreen {

    private float bgMin = 0.3f;
    private float bgMax = 0.4f;
    private float bgShift = 0.001f;
    private float bgCurrent = bgMin;
    private boolean isBgIncreasing = true;

    public BubbleRunnerScreen(IGameProcessor gameProcessor){
        super(gameProcessor);
        stage = new BubbleRunnerStage(this.gameProcessor);
    }

    //Screen
    @Override
    public void render(float delta) {
        if(isBgIncreasing){
            bgCurrent += bgShift;
        }else{
            bgCurrent -= bgShift;
        }

        if(isBgIncreasing && bgCurrent >= bgMax){
            isBgIncreasing = false;
        }else if(!isBgIncreasing && bgCurrent <= bgMin){
            isBgIncreasing = true;
        }

        super.render(delta);
    }

    @Override
    public void show() {
        super.show();
        if(stage != null){
            ((BubbleRunnerStage)stage).resume();
        }
    }
}
