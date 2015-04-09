package com.kasetagen.game.bubblerunner.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kasetagen.engine.IDataSaver;
import com.kasetagen.engine.IGameProcessor;
import com.kasetagen.engine.gdx.scenes.scene2d.ActorDecorator;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.AnimatedActor;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericActor;
import com.kasetagen.engine.gdx.scenes.scene2d.actors.GenericGroup;
import com.kasetagen.engine.gdx.scenes.scene2d.decorators.OscillatingDecorator;
import com.kasetagen.engine.gdx.scenes.scene2d.decorators.PulsingScaleDecorator;
import com.kasetagen.engine.gdx.scenes.scene2d.decorators.ShakeDecorator;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;
import com.kasetagen.game.bubblerunner.data.GameOptions;
import com.kasetagen.game.bubblerunner.scene2d.BaseStage;
import com.kasetagen.game.bubblerunner.scene2d.actor.DecoratedUIContainer;
import com.kasetagen.game.bubblerunner.util.AnimationUtil;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;
import com.kasetagen.game.bubblerunner.util.AtlasUtil;
import com.kasetagen.game.bubblerunner.util.ViewportUtil;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/25/14
 * Time: 10:59 PM
 */
public class BubbleRunnerMenu extends BaseBubbleRunnerScreen{

    protected IGameProcessor gameProcessor;

    private float buttonX = 175f;
    private float buttonY = 580f;
    private boolean isMenuView = true;
;
    private ImageButton startGameButton;
    private ImageButton optionsButton;
    private ActorDecorator infiniteLeftDecorator;

    private static final float MOON_WIDTH = 329f/2f;
    private static final float MOON_HEIGHT = 332f/2f;
    private static final float CLOUD_WIDTH = 4000f/2f;
    private static final float CLOUD_HEIGHT = 1000f/2f;

    private static final float EDISON_WIDTH = 400f/2f;
    private static final float EDISON_HEIGHT = 720f;

    private static final float EDYN_WIDTH = 800f/2f;
    private static final float EDYN_HEIGHT = 720f;

    private static final float TITLE_WIDTH = 1751f/2f;
    private static final float TITLE_HEIGHT = 456f/2f;

    private static final float PLAY_BTN_WIDTH = 1051f/2f;
    private static final float PLAY_BTN_HEIGHT = 226f/2f;
    private static final float OPTS_BTN_WIDTH = 1205f/2f;
    private static final float OPTS_BTN_HEIGHT = 175f/2f;

    private static final float EDISON_CYCLE_RATE = 1f/5f;
    private static final float EDYN_CYCLE_RATE = 1f/5f;
    private static final float EYE_CYCLE_RATE = 1f/12f;

    private static final int MAX_BLINK_INTERVAL = 10;
    private static final int MIN_BLINK_INTERVAL = 1;


    private Music bgMusic;
    private GenericGroup bgGroup;
    private GenericGroup menuGroup;
    private GenericGroup optionsGroup;

    private ClickListener listener;

    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 3; //Zero-based index, with 4 controls
    private static final float VOLUME_INCREMENT = 0.1f;

    private static final float TECHA_X = 40f;
    private static final float TECHA_Y = 480f;
    private static final float TECHA_W = 400f;
    private static final float TECHA_H = 215f;

    private static final float TECHB_X = 480f;
    private static final float TECHB_Y = 293f;
    private static final float TECHB_W = 537f;
    private static final float TECHB_H = 300f;


    private static final float CHAR_CIRCLE_SIZE = 200f;
    private static final float CHAR_CIRCLE_Y = 393f;
    private static final float EDYN_SELECT_X = 206f;
    private static final float EDISON_SELECT_X = 518f;
    private static final float EDYN_CIRCLE_X = 60f;
    private static final float EDYN_CIRCLE_Y = 368f;
    private static final float EDYN_CIRCLE_W = 375f;
    private static final float EDYN_CIRCLE_H = 250f;

    private static final float EDISON_CIRCLE_X = 492f;
    private static final float EDISON_CIRCLE_Y = 368f;
    private static final float EDISON_CIRCLE_W = 407f;
    private static final float EDISON_CIRCLE_H = 250f;
    private static final float MM_X = 900f;
    private static final float MM_Y = 300f;
    private static final float MM_W = 312f;
    private static final float MM_H = 50f;

    private ImageButton edisonSelect;
    private ImageButton edynSelect;
    private ImageButton mainMenuButton;
    private AnimatedActor edynCircle;
    private AnimatedActor eddyCircle;

    private PulsingScaleDecorator checkedMenuOptionDecorator;
    private DecoratedUIContainer startUiContainer;
    private DecoratedUIContainer optionsUiContainer;
    private int focusIndex = 0;
    private Sound sfx;
    private String charValue;
    private IDataSaver bgDataSaver, sfxDataSaver, charDataSaver;
    private Array<AnimatedActor> indicators;

    private static final float CHAR_SEL_X = 500f;
    private static final float CHAR_SEL_Y = 630f;
    private static final float CHAR_SEL_W = 505f;
    private static final float CHAR_SEL_H = 70f;

    private static final float MUSIC_X = 63f;
    private static final float MUSIC_Y = 175f;
    private static final float MUSIC_W = 212f;
    private static final float MUSIC_H = 62f;

    private static final float SFX_X = 50f;
    private static final float SFX_Y = 50f;
    private static final float SFX_W = 150f;
    private static final float SFX_H = 62f;

    private AnimatedActor charIndicator;

    //BG Volume
    private AnimatedActor bgIndicator;
    private Slider bgVolumeSet;

    //SFX Volume
    private AnimatedActor sfxIndicator;
    private Slider sfxVolumeSet;



    public BubbleRunnerMenu(IGameProcessor delegate){
        super(delegate);
        this.gameProcessor = delegate;
        stage = new BaseStage(delegate);

        bgMusic = gameProcessor.getAssetManager().get(AssetsUtil.MENU_BG_MUSIC, AssetsUtil.MUSIC);
        bgMusic.setVolume(gameProcessor.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY));
        bgMusic.play();
        bgMusic.setLooping(true);
        this.gameProcessor.setBGMusic(bgMusic);

        bgGroup = new GenericGroup(0f, 0f, ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT, null, Color.BLACK);
        menuGroup = new GenericGroup(0f, 0f, ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT, null, Color.BLACK);
        optionsGroup = new GenericGroup(ViewportUtil.VP_WIDTH, 0f, ViewportUtil.VP_WIDTH, ViewportUtil.VP_HEIGHT, null, Color.BLACK);
        stage.addActor(bgGroup);
        stage.addActor(menuGroup);
        stage.addActor(optionsGroup);

        TextureAtlas atlas = gameProcessor.getAssetManager().get(AssetsUtil.ANIMATION_ATLAS, AssetsUtil.TEXTURE_ATLAS);
        listener = new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Actor btn = event.getListenerActor();

                if(btn == startGameButton){
                    bgMusic.stop();
                    gameProcessor.changeToScreen(BubbleRunnerGame.RUNNER);

                } else if (btn == optionsButton){
                    showOptionsMenu();
                    //gameProcessor.changeToScreen(BubbleRunnerGame.OPTIONS);
                } else if (btn == edynSelect){
                    selectCharacter(true);
                } else if (btn == edisonSelect){
                    selectCharacter(false);
                } else if(btn == mainMenuButton){
                    showMainMenu();
                }
            }
        };

        indicators = new Array<AnimatedActor>();
        assembleMenuGroup(atlas);

        assembleOptionsGroup(atlas);

        indicators.add(charIndicator);
        indicators.add(bgIndicator);
        indicators.add(sfxIndicator);
    }

    private void assembleMenuGroup(TextureAtlas atlas) {

        infiniteLeftDecorator = new ActorDecorator() {
            @Override
            public void applyAdjustment(Actor actor, float v) {
                if(actor.getRight() <= 0f){
                    actor.setPosition(actor.getX()+(actor.getWidth()*2f), 0f);
                }
            }
        };

        checkedMenuOptionDecorator = new PulsingScaleDecorator(0.025f, 1f);

        float w = stage.getWidth();
        float h = stage.getHeight();
        //Add BG
        bgGroup.addActor(new GenericActor(0f, 0f, w, h, atlas.findRegion(AtlasUtil.ANI_TITLE_BG), Color.BLACK));

        GenericActor moon = new GenericActor(600f, 500f, MOON_WIDTH, MOON_HEIGHT, atlas.findRegion(AtlasUtil.ANI_TITLE_MOON), Color.YELLOW);
        moon.addDecorator(new ShakeDecorator(5f, 5f, 4f));
        moon.addDecorator(new OscillatingDecorator(5f, 10f, 2.5f));
        bgGroup.addActor(moon);
        //add Clouds5 setup
        addClouds(atlas.findRegion(AtlasUtil.ANI_TITLE_C5), -25f);

        //Add Skyline2
        bgGroup.addActor(new GenericActor(0f, 0f, w, h, atlas.findRegion(AtlasUtil.ANI_TITLE_SKYLINE2), Color.BLACK));

        //Add Cloud 4
        addClouds(atlas.findRegion(AtlasUtil.ANI_TITLE_C4), -25f);

        //Add Cloud 3
        addClouds(atlas.findRegion(AtlasUtil.ANI_TITLE_C3), -75f);

        //Add Skyline1
        bgGroup.addActor(new GenericActor(0f, 0f, w, h, atlas.findRegion(AtlasUtil.ANI_TITLE_SKYLINE1), Color.BLACK));

        //Add Title
        bgGroup.addActor(new GenericActor(150f, 25f, TITLE_WIDTH, TITLE_HEIGHT, atlas.findRegion(AtlasUtil.ANI_TITLE_TITLE), Color.WHITE));

        //Add Cloud 2
        addClouds(atlas.findRegion(AtlasUtil.ANI_TITLE_C2), -100);


        //Add Cloud 1
        addClouds(atlas.findRegion(AtlasUtil.ANI_TITLE_C1), -125f);

        //Add Platform
        menuGroup.addActor(new GenericActor(0f, 0f, w, h, atlas.findRegion(AtlasUtil.ANI_TITLE_PLATFORM), Color.BLACK));

        //Add Edison
        Animation eddyAni = new Animation(EDISON_CYCLE_RATE, atlas.findRegions(AtlasUtil.ANI_TITLE_EDISON));
        menuGroup.addActor(new AnimatedActor(0f, 0f, EDISON_WIDTH, EDISON_HEIGHT, eddyAni, 0f));
        //Add Edyn
        Animation edynAni = new Animation(EDYN_CYCLE_RATE, atlas.findRegions(AtlasUtil.ANI_TITLE_EDYN));
        menuGroup.addActor(new AnimatedActor(w-EDYN_WIDTH, 0f, EDYN_WIDTH, EDYN_HEIGHT, edynAni, 0f));

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
                     }
                 }
            }
        });

        menuGroup.addActor(eyes);

        Array<TextureAtlas.AtlasRegion> escapeImgs = atlas.findRegions(AtlasUtil.ANI_TITLE_PLAY_BTN);
        TextureRegionDrawable escapeUp = new TextureRegionDrawable(escapeImgs.get(0));
        TextureRegionDrawable escapeDown = new TextureRegionDrawable(escapeImgs.get(1));

        startGameButton = new ImageButton(escapeUp, escapeDown, escapeDown);
        startGameButton.setSize(PLAY_BTN_WIDTH, PLAY_BTN_HEIGHT);
        startGameButton.addListener(listener);
        //startGameButton.setPosition(buttonX, buttonY);
        startGameButton.setChecked(true);

        startUiContainer = new DecoratedUIContainer(startGameButton);
        startUiContainer.setSize(PLAY_BTN_WIDTH, PLAY_BTN_HEIGHT);
        startUiContainer.setPosition(buttonX, buttonY);
        startUiContainer.addDecorator(checkedMenuOptionDecorator);
        menuGroup.addActor(startUiContainer);


        Array<TextureAtlas.AtlasRegion> optionsImgs = atlas.findRegions(AtlasUtil.ANI_TITLE_OPT_BTN);
        TextureRegionDrawable optionsUp = new TextureRegionDrawable(optionsImgs.get(0));
        TextureRegionDrawable optionsDown = new TextureRegionDrawable(optionsImgs.get(1));
        optionsButton = new ImageButton(optionsUp, optionsDown, optionsDown);
        optionsButton.setSize(OPTS_BTN_WIDTH, OPTS_BTN_HEIGHT);

        //optionsButton.setPosition(buttonX, buttonY - (optionsButton.getHeight()));
        optionsButton.addListener(listener);
        optionsUiContainer = new DecoratedUIContainer(optionsButton);
        optionsUiContainer.setPosition(buttonX, buttonY - (PLAY_BTN_HEIGHT));
        optionsUiContainer.setSize(OPTS_BTN_WIDTH, OPTS_BTN_HEIGHT);
        //optUiContainer.addDecorator(checkedMenuOptionDecorator);
        menuGroup.addActor(optionsUiContainer);
    }

    private void assembleOptionsGroup(TextureAtlas atlas){

        /*
         * Gather values
         */
        float sfxVolValue = gameProcessor.getStoredFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY);
        sfxVolValue = sfxVolValue < 0f ? 0f : sfxVolValue;
        float bgVolValue = gameProcessor.getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY);
        bgVolValue = bgVolValue < 0f ? 0f : bgVolValue;

        charValue = gameProcessor.getStoredString(GameOptions.CHARACTER_SELECT_KEY);
        if(charValue == null || "".equals(charValue.trim())){
            charValue = AnimationUtil.CHARACTER_2;
        }

        /*
         * Gather Reference Interatctions
         */
        Skin skin = gameProcessor.getAssetManager().get(AssetsUtil.DEFAULT_SKIN, AssetsUtil.SKIN);
        sfx = gameProcessor.getAssetManager().get(AssetsUtil.SND_SHIELD_ON, AssetsUtil.SOUND);

        /*
         * Add Scaffolding
         */
        GenericActor runnerScaffold = new GenericActor(0f, 0f, optionsGroup.getWidth(), optionsGroup.getHeight(),
                    atlas.findRegion(AtlasUtil.ANI_OPTIONS_RUNNERSCAFFOLD), Color.WHITE);
        optionsGroup.addActor(runnerScaffold);

        GenericActor volumeScaffold = new GenericActor(0f, 0f, optionsGroup.getWidth(), optionsGroup.getHeight(),
                atlas.findRegion(AtlasUtil.ANI_OPTIONS_VOLUMESCAFFOLD), Color.WHITE);
        optionsGroup.addActor(volumeScaffold);

        Animation techAAni = new Animation(1f/2f, atlas.findRegions(AtlasUtil.ANI_OPTIONS_TECH_A));
        AnimatedActor techA = new AnimatedActor(TECHA_X, TECHA_Y, TECHA_W, TECHA_H, techAAni, 0f);
        optionsGroup.addActor(techA);
        Animation techBAni = new Animation(1f/2f, atlas.findRegions(AtlasUtil.ANI_OPTIONS_TECH_B));
        AnimatedActor techB = new AnimatedActor(TECHB_X, TECHB_Y, TECHB_W, TECHB_H, techBAni, 0f);
        optionsGroup.addActor(techB);

        /*
         * Add Player Buttons
         */
        Animation charAni = new Animation(1f, atlas.findRegions(AtlasUtil.ANI_OPTIONS_CHARSELECT));
        charIndicator = new AnimatedActor(CHAR_SEL_X, CHAR_SEL_Y, CHAR_SEL_W, CHAR_SEL_H, charAni, 0f);
        charIndicator.setTargetKeyFrame(1);
        optionsGroup.addActor(charIndicator);

        Animation edynDefCircleAni = new Animation(0.5f/10f, atlas.findRegions(AtlasUtil.ANI_OPTIONS_EDYN_SELECT), Animation.PlayMode.REVERSED);
        Animation edynCircleAni = new Animation(0.5f/10f, atlas.findRegions(AtlasUtil.ANI_OPTIONS_EDYN_SELECT));
        edynCircle = new AnimatedActor(EDYN_CIRCLE_X, EDYN_CIRCLE_Y, EDYN_CIRCLE_W, EDYN_CIRCLE_H, edynDefCircleAni, 0f);
        edynCircle.addStateAnimation("SELECTED", edynCircleAni);
        edynCircle.setIsLooping(false);
        optionsGroup.addActor(edynCircle);
        Array<TextureAtlas.AtlasRegion> edynImgs = atlas.findRegions(AtlasUtil.ANI_OPTIONS_EDYN_EYES);
        TextureRegionDrawable edynUp = new TextureRegionDrawable(edynImgs.get(0));
        TextureRegionDrawable edynDown = new TextureRegionDrawable(edynImgs.get(1));
        edynSelect = new ImageButton(edynUp, edynDown, edynDown);

        edynSelect.setSize(CHAR_CIRCLE_SIZE, CHAR_CIRCLE_SIZE);
        edynSelect.setPosition(EDYN_SELECT_X, CHAR_CIRCLE_Y);
        edynSelect.addListener(listener);
        edynSelect.setChecked(AnimationUtil.CHARACTER_2.equals(charValue));
        optionsGroup.addActor(edynSelect);


        Animation eddyDefCircleAni = new Animation(0.5f/10f, atlas.findRegions(AtlasUtil.ANI_OPTIONS_EDISON_SELECT), Animation.PlayMode.REVERSED);
        Animation eddyCircleAni = new Animation(0.5f/10f, atlas.findRegions(AtlasUtil.ANI_OPTIONS_EDISON_SELECT));
        eddyCircle = new AnimatedActor(EDISON_CIRCLE_X, EDISON_CIRCLE_Y, EDISON_CIRCLE_W, EDISON_CIRCLE_H, eddyDefCircleAni, 0f);
        eddyCircle.addStateAnimation("SELECTED", eddyCircleAni);
        eddyCircle.setIsLooping(false);
        optionsGroup.addActor(eddyCircle);

        Array<TextureAtlas.AtlasRegion> edisonImgs = atlas.findRegions(AtlasUtil.ANI_OPTIONS_EDISON_EYES);
        TextureRegionDrawable edisonUp = new TextureRegionDrawable(edisonImgs.get(0));
        TextureRegionDrawable edisonDown = new TextureRegionDrawable(edisonImgs.get(1));
        edisonSelect = new ImageButton(edisonUp, edisonDown, edisonDown);
        edisonSelect.setSize(CHAR_CIRCLE_SIZE, CHAR_CIRCLE_SIZE);
        edisonSelect.setPosition(EDISON_SELECT_X, CHAR_CIRCLE_Y);
        edisonSelect.addListener(listener);
        edisonSelect.setChecked(AnimationUtil.CHARACTER_1.equals(charValue));
        optionsGroup.addActor(edisonSelect);

        charDataSaver = new IDataSaver() {
            @Override
            public void updatePreferences(Preferences prefs) {
                prefs.putString(GameOptions.CHARACTER_SELECT_KEY, charValue);
            }
        };

        /*
         * Add Main Menu Button
         */
        Array<TextureAtlas.AtlasRegion> mmImgs = atlas.findRegions(AtlasUtil.ANI_OPTIONS_MAINMENU);
        TextureRegionDrawable mmDown = new TextureRegionDrawable(mmImgs.get(1));
        mainMenuButton = new ImageButton(new TextureRegionDrawable(mmImgs.get(0)), mmDown, mmDown);
        mainMenuButton.setSize(MM_W, MM_H);
        mainMenuButton.setPosition(MM_X, MM_Y);
        mainMenuButton.addListener(listener);
        optionsGroup.addActor(mainMenuButton);


        /*
         * Add Sliders
         */
        Animation bgAni = new Animation(1f, atlas.findRegions(AtlasUtil.ANI_OPTIONS_MUSIC));
        bgIndicator = new AnimatedActor(MUSIC_X, MUSIC_Y, MUSIC_W, MUSIC_H, bgAni, 0f);
        bgIndicator.setTargetKeyFrame(0);
        optionsGroup.addActor(bgIndicator);

        bgVolumeSet = new Slider(0f, 1f, 0.1f, false, skin);
        bgVolumeSet.setValue(bgVolValue);
        bgVolumeSet.setPosition(319f, 190f);
        bgVolumeSet.setSize(500f, 20f);
        optionsGroup.addActor(bgVolumeSet);
        bgDataSaver = new IDataSaver() {
            @Override
            public void updatePreferences(Preferences prefs) {
                prefs.putFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY, bgVolumeSet.getValue());
            }
        };

        bgVolumeSet.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                gameProcessor.saveGameData(bgDataSaver);
                gameProcessor.setBGMusicVolume(bgVolumeSet.getValue());
            }
        });


        Animation sfxAni = new Animation(1f, atlas.findRegions(AtlasUtil.ANI_OPTIONS_SFX));
        sfxIndicator = new AnimatedActor(SFX_X, SFX_Y, SFX_W, SFX_H, sfxAni, 0f);
        sfxIndicator.setTargetKeyFrame(0);
        optionsGroup.addActor(sfxIndicator);

        sfxVolumeSet = new Slider(0f, 1f, 0.1f, false, skin);
        sfxVolumeSet.setValue(sfxVolValue);
        sfxVolumeSet.setPosition(208f, 68f);
        sfxVolumeSet.setSize(500f, 20f);
        optionsGroup.addActor(sfxVolumeSet);
        sfxDataSaver = new IDataSaver() {
            @Override
            public void updatePreferences(Preferences prefs) {
                prefs.putFloat(GameOptions.SFX_MUSIC_VOLUME_PREF_KEY, sfxVolumeSet.getValue());
            }
        };

        sfxVolumeSet.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                gameProcessor.saveGameData(sfxDataSaver);
                sfx.play(sfxVolumeSet.getValue());
            }
        });

        selectCharacter(edynSelect.isChecked());
    }

    private void addClouds(TextureAtlas.AtlasRegion cloudRegion, float speed){
        GenericActor c1 = new GenericActor(0f, 0f, CLOUD_WIDTH, CLOUD_HEIGHT, cloudRegion, Color.BLACK);
        c1.velocity.x = speed;
        GenericActor c2 = new GenericActor(c1.getRight(), 0f, CLOUD_WIDTH, CLOUD_HEIGHT, cloudRegion, Color.BLACK);
        c2.velocity.x = speed;
        //--Decorate
        c1.addDecorator(infiniteLeftDecorator);
        c2.addDecorator(infiniteLeftDecorator);
        bgGroup.addActor(c1);
        bgGroup.addActor(c2);
    }

    private void showOptionsMenu() {
        menuGroup.addAction(Actions.sequence(
                Actions.moveTo(-1280f, 0f, 1f, Interpolation.fade),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        optionsGroup.addAction(Actions.moveTo(0f, 0f, 1f, Interpolation.swingOut));
                        isMenuView = false;
                    }
                })));
    }

    private void showMainMenu(){
        optionsGroup.addAction(Actions.sequence(
                Actions.moveTo(1280f, 0f, 1f, Interpolation.swingIn),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        menuGroup.addAction(Actions.moveTo(0f, 0f, 1f, Interpolation.fade));
                        isMenuView = true;
                    }
                })));
    }

    private void selectCharacter(boolean isLeft){
        if(isLeft){
            charValue = AnimationUtil.CHARACTER_2;
            edynSelect.setChecked(true);
            edisonSelect.setChecked(false);
            eddyCircle.setState(AnimatedActor.DEFAULT_STATE, true);
            edynCircle.setState("SELECTED", true);
        }else{
            charValue = AnimationUtil.CHARACTER_1;
            edynSelect.setChecked(false);
            edisonSelect.setChecked(true);
            edynCircle.setState(AnimatedActor.DEFAULT_STATE, true);
            eddyCircle.setState("SELECTED", true);

        }

        gameProcessor.saveGameData(charDataSaver);
    }

    private void highlightIndicator(int index){
        for(int i=0;i<indicators.size;i++){
            if(i != index){
                indicators.get(i).setTargetKeyFrame(0);
            }else{
                indicators.get(i).setTargetKeyFrame(1);
            }
        }
    }

    private void selectMenuItem(int index){
        //setDefaultColors();
        highlightIndicator(index);
        switch(index){
            case 0:
            case 1:
            case 2:
                mainMenuButton.setChecked(false);
                break;
            case 3:
                mainMenuButton.setChecked(true);
                break;
            default:
                break;
        }
    }

    public void handleRightAdjust(int index){
        switch(index){
            case 0:
                selectCharacter(false);
                break;
            case 1:
                bgVolumeSet.setValue(bgVolumeSet.getValue() + VOLUME_INCREMENT);
                break;
            case 2:
                sfxVolumeSet.setValue(sfxVolumeSet.getValue() + VOLUME_INCREMENT);
                break;
            default:
                break;
        }
    }

    public void handleLeftAdjust(int index){
        switch(index){
            case 0:
                selectCharacter(true);
                break;
            case 1:
                bgVolumeSet.setValue(bgVolumeSet.getValue() - VOLUME_INCREMENT);
                break;
            case 2:
                sfxVolumeSet.setValue(sfxVolumeSet.getValue() - VOLUME_INCREMENT);
                break;
            default:
                break;
        }
    }

///
/// Screen
///
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

        boolean trapped = false;
        if(isMenuView){
            if(keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER){
                if(startGameButton.isChecked()){
                    bgMusic.stop();
                    trapped = true;
                    gameProcessor.changeToScreen(BubbleRunnerGame.RUNNER);
                }else if(optionsButton.isChecked()){
                    trapped = true;
                    showOptionsMenu();
                }
            }else if(keycode == Input.Keys.DOWN){
                trapped = true;
                optionsButton.setChecked(true);
                optionsUiContainer.addDecorator(checkedMenuOptionDecorator);
                startGameButton.setChecked(false);
                startUiContainer.removeDecorator(checkedMenuOptionDecorator);
                startUiContainer.setScale(1f);

            }else if(keycode == Input.Keys.UP){
                trapped = true;
                startGameButton.setChecked(true);
                startUiContainer.addDecorator(checkedMenuOptionDecorator);
                optionsButton.setChecked(false);
                optionsUiContainer.removeDecorator(checkedMenuOptionDecorator);
                optionsUiContainer.setScale(1f);
            }
        }else{
            if(keycode == Input.Keys.DOWN){
                trapped = true;
                if(focusIndex < MAX_INDEX){
                    focusIndex++;
                }
                selectMenuItem(focusIndex);
            }else if(keycode == Input.Keys.UP){
                trapped = true;
                if(focusIndex > MIN_INDEX){
                    focusIndex--;
                }
                selectMenuItem(focusIndex);
            }else if(keycode == Input.Keys.RIGHT){
                trapped = true;
                //Adjust value right
                handleRightAdjust(focusIndex);
            }else if(keycode == Input.Keys.LEFT){
                trapped = true;
                //Adjust value left
                handleLeftAdjust(focusIndex);
            }else if(keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER){
                trapped = true;
                //if return button go home.
                showMainMenu();
            }
        }

        return trapped;
    }

//
//IStageManager
//
    public Stage getStage(){
        return stage;
    }
}
