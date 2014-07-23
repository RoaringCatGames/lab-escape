package com.kasetagen.game.bubblerunner.screen;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;
import com.kasetagen.game.bubblerunner.data.GameOptions;
import com.kasetagen.game.bubblerunner.delegate.IGameProcessor;
import com.kasetagen.game.bubblerunner.delegate.IStageManager;
import com.kasetagen.game.bubblerunner.scene2d.actor.GenericActor;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/25/14
 * Time: 10:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class BubbleRunnerMenu extends ApplicationAdapter implements Screen, InputProcessor, IStageManager{

    protected IGameProcessor gameProcessor;

    private Stage stage;
    private TextButton startGameButton;
    private TextButton optionsButton;
    private TextureRegion bgTextureRegion;


    private Music bgMusic;
    public BubbleRunnerMenu(IGameProcessor delegate){
        this.gameProcessor = delegate;
        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        bgMusic = delegate.getAssetManager().get(AssetsUtil.BACKGROUND_SOUND, AssetsUtil.MUSIC);
        bgMusic.setVolume(delegate.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY));
        bgMusic.play();


        ClickListener listener = new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                bgMusic.stop();;
                Actor btn = event.getListenerActor();

                if(btn == startGameButton){
                    gameProcessor.changeToScreen(BubbleRunnerGame.RUNNER);

                }
                else if (btn == optionsButton){
                    gameProcessor.changeToScreen(BubbleRunnerGame.OPTIONS);
                }

                Gdx.app.log("Menu Item Clicked", "Clicked");
            }
        };

        bgTextureRegion = new TextureRegion(gameProcessor.getAssetManager().get(AssetsUtil.TITLE_SCREEN, AssetsUtil.TEXTURE));
        stage.addActor(new GenericActor(0, 0, stage.getWidth(), stage.getHeight(), bgTextureRegion, Color.DARK_GRAY));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = gameProcessor.getAssetManager().get(AssetsUtil.COURIER_FONT_32, AssetsUtil.BITMAP_FONT);
        style.fontColor =  Color.CYAN;
        style.overFontColor = Color.RED;
        style.downFontColor = Color.GRAY;
        float fontScale = 2f;
        style.font.setScale(fontScale);

        startGameButton = new TextButton("Play Game", style);
        startGameButton.addListener(listener);
        startGameButton.setPosition(((stage.getWidth()/4) * 3) - startGameButton.getWidth()/2, (stage.getHeight()/4));

        stage.addActor(startGameButton);

        optionsButton = new TextButton("Options", style);
        optionsButton.setPosition(((stage.getWidth() / 4) * 3) - startGameButton.getWidth() / 2, (stage.getHeight() / 8));
        optionsButton.addListener(listener);

        stage.addActor(optionsButton);

    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        //To change body of implemented methods use File | Settings | File Templates.
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
    public void pause() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void resume() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


//
//Input Processor
//
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

//
//IStageManager
//
    public Stage getStage(){
        return stage;
    }
}
