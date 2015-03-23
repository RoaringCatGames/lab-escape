package com.kasetagen.game.bubblerunner.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kasetagen.engine.IGameProcessor;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.AnimatedActor;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;
import com.kasetagen.game.bubblerunner.data.GameOptions;
import com.kasetagen.game.bubblerunner.scene2d.BaseStage;
import com.kasetagen.game.bubblerunner.util.AnimationUtil;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/25/14
 * Time: 10:59 PM
 */
public class BubbleRunnerMenu extends BaseBubbleRunnerScreen{

    protected IGameProcessor gameProcessor;

    private float buttonX = 250f;
    private float buttonY = 600f;

    private TextButton startGameButton;
    private TextButton optionsButton;


    private Music bgMusic;
    public BubbleRunnerMenu(IGameProcessor delegate){
        super(delegate);
        this.gameProcessor = delegate;
        stage = new BaseStage(delegate);

        bgMusic = delegate.getAssetManager().get(AssetsUtil.EIGHT_BIT_BKG_MUSIC, AssetsUtil.MUSIC);
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

        TextureAtlas atlas = gameProcessor.getAssetManager().get(AssetsUtil.ANIMATION_ATLAS, AssetsUtil.TEXTURE_ATLAS);
        Animation titleAni = new Animation(AnimationUtil.TITLE_CYCLE_RATE, atlas.findRegions("screens/Title"));
        stage.addActor(new AnimatedActor(0, 0, stage.getWidth(), stage.getHeight(), titleAni,0f));


        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = gameProcessor.getAssetManager().get(AssetsUtil.REXLIA_48, AssetsUtil.BITMAP_FONT);
        style.fontColor =  Color.YELLOW;
        style.overFontColor = Color.RED;
        style.checkedFontColor = Color.RED;
        style.downFontColor = Color.GRAY;



        startGameButton = new TextButton("Escape!", style);
        startGameButton.addListener(listener);
        startGameButton.setPosition(buttonX, buttonY);
        startGameButton.setChecked(true);
        stage.addActor(startGameButton);

        optionsButton = new TextButton("Options", style);
        optionsButton.setPosition(buttonX, buttonY - (startGameButton.getHeight()*1.25f));
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
        Gdx.app.log("MENU", "KeyDown Fired!");
        if(keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER){
            if(startGameButton.isChecked()){
                bgMusic.stop();
                gameProcessor.changeToScreen(BubbleRunnerGame.RUNNER);
            }else if(optionsButton.isChecked()){
                gameProcessor.changeToScreen(BubbleRunnerGame.OPTIONS);
            }
        }

        if(keycode == Input.Keys.DOWN){
            optionsButton.setChecked(true);
            startGameButton.setChecked(false);
        }

        if(keycode == Input.Keys.UP){
            startGameButton.setChecked(true);
            optionsButton.setChecked(false);
        }

        return true;
    }

//
//IStageManager
//
    public Stage getStage(){
        return stage;
    }
}
