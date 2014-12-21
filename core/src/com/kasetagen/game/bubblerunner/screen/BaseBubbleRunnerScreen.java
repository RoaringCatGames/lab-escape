package com.kasetagen.game.bubblerunner.screen;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kasetagen.engine.IGameProcessor;
import com.kasetagen.game.bubblerunner.delegate.IStageManager;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 7/20/14
 * Time: 8:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseBubbleRunnerScreen extends ApplicationAdapter implements Screen, InputProcessor, IStageManager {

    protected IGameProcessor gameProcessor;
    protected Stage stage;


    public BaseBubbleRunnerScreen(IGameProcessor delegate){
        gameProcessor = delegate;
    }

    @Override
    public Stage getStage() {
        return stage;  //To change body of implemented methods use File | Settings | File Templates.
    }

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

    @Override
    public void render(float delta) {
        if(stage != null){
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void show() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void hide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("Screen Resizing", "Resizing: " + width + "x" + height);
        stage.getViewport().update(width, height);
        super.resize(width, height);
    }
}
