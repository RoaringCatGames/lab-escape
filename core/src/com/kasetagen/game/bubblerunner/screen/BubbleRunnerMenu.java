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
import com.kasetagen.engine.gdx.scenes.scene2d.ActorDecorator;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.AnimatedActor;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericActor;
import com.kasetagen.engine.gdx.scenes.scene2d.decorators.OscillatingDecorator;
import com.kasetagen.engine.gdx.scenes.scene2d.decorators.ShakeDecorator;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;
import com.kasetagen.game.bubblerunner.data.GameOptions;
import com.kasetagen.game.bubblerunner.scene2d.BaseStage;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;
import com.kasetagen.game.bubblerunner.util.AtlasUtil;

import java.util.Random;

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
    private ActorDecorator infiniteLeftDecorator;

    private static final float MOON_WIDTH = 329f/2f;
    private static final float MOON_HEIGHT = 332f/2f;
    private static final float CLOUD_WIDTH = 4000f/2f;
    private static final float CLOUD_HEIGHT = 1000f/2f;

    private static final float EDISON_WIDTH = 400f/2f;
    private static final float EDISON_HEIGHT = 720f;

    private static final float EDYN_WIDTH = 800f/2f;
    private static final float EDYN_HEIGHT = 720f;

    private static final float EDISON_CYCLE_RATE = 1f/5f;
    private static final float EDYN_CYCLE_RATE = 1f/5f;
    private static final float EYE_CYCLE_RATE = 1f/12f;

    private static final int MAX_BLINK_INTERVAL = 10;
    private static final int MIN_BLINK_INTERVAL = 1;


    private Music bgMusic;
    public BubbleRunnerMenu(IGameProcessor delegate){
        super(delegate);
        this.gameProcessor = delegate;
        stage = new BaseStage(delegate);

        bgMusic = delegate.getAssetManager().get(AssetsUtil.EIGHT_BIT_BKG_MUSIC, AssetsUtil.MUSIC);
        bgMusic.setVolume(delegate.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY));
        bgMusic.play();
        this.gameProcessor.setBGMusic(bgMusic);


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

        infiniteLeftDecorator = new ActorDecorator() {
            @Override
            public void applyAdjustment(Actor actor, float v) {
                if(actor.getRight() <= 0f){
                    actor.setPosition(actor.getX()+(actor.getWidth()*2f), 0f);
                }
            }
        };

        float w = stage.getWidth();
        float h = stage.getHeight();
        //Add BG
        stage.addActor(new GenericActor(0f, 0f, w, h, atlas.findRegion(AtlasUtil.ANI_TITLE_BG), Color.BLACK));

        GenericActor moon = new GenericActor(400f, 400f, MOON_WIDTH, MOON_HEIGHT, atlas.findRegion(AtlasUtil.ANI_TITLE_MOON), Color.YELLOW);
        moon.addDecorator(new ShakeDecorator(5f, 5f, 2f));
        moon.addDecorator(new OscillatingDecorator(5f, 10f, 5f));
        stage.addActor(moon);
        //add Clouds5 setup
        addClouds(atlas.findRegion(AtlasUtil.ANI_TITLE_C5), -25f);

        //Add Skyline2
        stage.addActor(new GenericActor(0f, 0f, w, h, atlas.findRegion(AtlasUtil.ANI_TITLE_SKYLINE2), Color.BLACK));

        //Add Cloud 4
        addClouds(atlas.findRegion(AtlasUtil.ANI_TITLE_C4), -25f);

        //Add Cloud 3
        addClouds(atlas.findRegion(AtlasUtil.ANI_TITLE_C3), -75f);

        //Add Skyline1
        stage.addActor(new GenericActor(0f, 0f, w, h, atlas.findRegion(AtlasUtil.ANI_TITLE_SKYLINE1), Color.BLACK));

        //Add Cloud 2
        addClouds(atlas.findRegion(AtlasUtil.ANI_TITLE_C2), -100);

        //Add Title
        stage.addActor(new GenericActor(0f, 0f, w, h, atlas.findRegion(AtlasUtil.ANI_TITLE_TITLE), Color.WHITE));

        //Add Cloud 1
        addClouds(atlas.findRegion(AtlasUtil.ANI_TITLE_C1), -125f);

        //Add Platform
        stage.addActor(new GenericActor(0f, 0f, w, h, atlas.findRegion(AtlasUtil.ANI_TITLE_PLATFORM), Color.BLACK));

        //Add Edison
        Animation eddyAni = new Animation(EDISON_CYCLE_RATE, atlas.findRegions(AtlasUtil.ANI_TITLE_EDISON));
        stage.addActor(new AnimatedActor(0f, 0f, EDISON_WIDTH, EDISON_HEIGHT, eddyAni, 0f));
        //Add Edyn
        Animation edynAni = new Animation(EDYN_CYCLE_RATE, atlas.findRegions(AtlasUtil.ANI_TITLE_EDYN));
        stage.addActor(new AnimatedActor(w-EDYN_WIDTH, 0f, EDYN_WIDTH, EDYN_HEIGHT, edynAni, 0f));

        Animation edynEyes = new Animation(EYE_CYCLE_RATE, atlas.findRegions(AtlasUtil.ANI_TITLE_EDYN_EYES));
        AnimatedActor eyes = new AnimatedActor(w-EDYN_WIDTH, 0f, EDYN_WIDTH, EDYN_HEIGHT, edynEyes, 0f);
        eyes.setIsLooping(false);
        eyes.addDecorator(new ActorDecorator() {
            private float secondsBeforeBlink = 0f;
            private float elapsedSeconds = 0f;
            private Random rand = new Random(System.currentTimeMillis());
            @Override
            public void applyAdjustment(Actor actor, float v) {
                 AnimatedActor a = ((AnimatedActor)actor);

                 if(a.isAnimationComplete()){
                     elapsedSeconds += v;
                     if(elapsedSeconds >= secondsBeforeBlink){
                         a.setState(AnimatedActor.DEFAULT_STATE, true);
                         elapsedSeconds = 0f;
                         secondsBeforeBlink = rand.nextInt(MAX_BLINK_INTERVAL)+MIN_BLINK_INTERVAL;
                         Gdx.app.log("EYES", "New Interval: " + secondsBeforeBlink);
                     }
                 }
            }
        });

        stage.addActor(eyes);


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

    private void addClouds(TextureAtlas.AtlasRegion cloudRegion, float speed){
        GenericActor c1 = new GenericActor(0f, 0f, CLOUD_WIDTH, CLOUD_HEIGHT, cloudRegion, Color.BLACK);
        c1.velocity.x = speed;
        GenericActor c2 = new GenericActor(c1.getRight(), 0f, CLOUD_WIDTH, CLOUD_HEIGHT, cloudRegion, Color.BLACK);
        c2.velocity.x = speed;
        //--Decorate
        c1.addDecorator(infiniteLeftDecorator);
        c2.addDecorator(infiniteLeftDecorator);
        stage.addActor(c1);
        stage.addActor(c2);
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
        if(!bgMusic.isPlaying())
            bgMusic.play();
        gameProcessor.setBGMusic(bgMusic);
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
