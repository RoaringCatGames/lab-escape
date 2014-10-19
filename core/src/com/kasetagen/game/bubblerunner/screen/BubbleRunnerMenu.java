package com.kasetagen.game.bubblerunner.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;
import com.kasetagen.game.bubblerunner.data.GameOptions;
import com.kasetagen.game.bubblerunner.delegate.IGameProcessor;
import com.kasetagen.game.bubblerunner.delegate.IStageManager;
import com.kasetagen.game.bubblerunner.scene2d.BaseStage;
import com.kasetagen.game.bubblerunner.scene2d.actor.GenericActor;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;
import com.kasetagen.game.bubblerunner.util.ViewportUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/25/14
 * Time: 10:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class BubbleRunnerMenu extends BaseBubbleRunnerScreen{//ApplicationAdapter implements Screen, InputProcessor, IStageManager{

    protected IGameProcessor gameProcessor;

    private TextButton startGameButton;
    private TextButton optionsButton;
    private TextureRegion bgTextureRegion;


    private Music bgMusic;
    public BubbleRunnerMenu(IGameProcessor delegate){
        super(delegate);
        this.gameProcessor = delegate;
        stage = new BaseStage();

        bgMusic = delegate.getAssetManager().get(AssetsUtil.BACKGROUND_SOUND, AssetsUtil.MUSIC);
        bgMusic.setVolume(delegate.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY));
        bgMusic.play();


        ClickListener listener = new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                bgMusic.stop();
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
        style.font = gameProcessor.getAssetManager().get(AssetsUtil.REXLIA_32, AssetsUtil.BITMAP_FONT); //COURIER_FONT_32, AssetsUtil.BITMAP_FONT);
        style.fontColor =  Color.CYAN;
        style.overFontColor = Color.RED;
        style.downFontColor = Color.GRAY;
        float fontScale = 2f;
        style.font.setScale(fontScale);

        startGameButton = new TextButton("Touch to Start", style);
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

        if(Gdx.graphics.isFullscreen()){
            Viewport vp = stage.getViewport();
            int screenW = vp.getScreenWidth();
            int screenH = vp.getScreenHeight();
            int leftCrop = vp.getLeftGutterWidth();
            int bottomCrop = vp.getBottomGutterHeight();
            int xPos = leftCrop;
            int yPos = bottomCrop;

            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            Gdx.gl.glViewport(xPos, yPos, screenW, screenH);
        }
        stage.draw();
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }


//
//Input Processor
//
    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.SPACE){
            bgMusic.stop();
            gameProcessor.changeToScreen(BubbleRunnerGame.RUNNER);
        }

        return false;
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
