package com.kasetagen.game.bubblerunner.screen;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kasetagen.game.bubblerunner.delegate.IGameProcessor;
import com.kasetagen.game.bubblerunner.delegate.IStageManager;
import com.kasetagen.game.bubblerunner.scene2d.BubbleRunnerStage;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/27/14
 * Time: 7:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class BubbleRunnerScreen extends ApplicationAdapter implements Screen, InputProcessor, IStageManager {

    private float bgMin = 0.2f;
    private float bgMax = .5f;
    private float bgShift = 0.005f;
    private float bgCurrent = bgMin;
    private boolean isBgIncreasing = true;

    BubbleRunnerStage stage;
    IGameProcessor processor;

    public BubbleRunnerScreen(IGameProcessor gameProcessor){
        processor = gameProcessor;
        stage = new BubbleRunnerStage(processor);
    }

    //IStageManager
    @Override
    public Stage getStage() {
        return stage;
    }

    //InputProcessor
    @Override
    public boolean keyDown(int keycode) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean keyTyped(char character) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean scrolled(int amount) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //Screen
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(bgCurrent, bgCurrent, bgCurrent, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void hide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
