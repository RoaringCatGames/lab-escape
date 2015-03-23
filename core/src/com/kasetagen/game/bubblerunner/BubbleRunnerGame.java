package com.kasetagen.game.bubblerunner;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.kasetagen.engine.IDataSaver;
import com.kasetagen.engine.IGameProcessor;
import com.kasetagen.engine.gdx.scenes.scene2d.KasetagenStateUtil;
import com.kasetagen.game.bubblerunner.screen.BubbleRunnerMenu;
import com.kasetagen.game.bubblerunner.screen.BubbleRunnerOptionsMenu;
import com.kasetagen.game.bubblerunner.screen.BubbleRunnerScreen;
import com.kasetagen.game.bubblerunner.screen.LoadingScreen;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;

public class BubbleRunnerGame extends Game implements IGameProcessor {

    public static final String LOADING = "loading";
    public static final String OPTIONS = "options";
    public static final String MENU = "menu";
    public static final String RUNNER = "runner";

    public static final String BUBBLE_RUNNER_DATA_NAME = "BubbleRunnerData";

    LoadingScreen loading;
    BubbleRunnerOptionsMenu options;
    BubbleRunnerMenu menu;
    BubbleRunnerScreen runnerScreen;
    InputMultiplexer input;

    boolean isInitialized = false;
    protected AssetManager assetManager;
    boolean isControllerEnabled = false;

    public BubbleRunnerGame(boolean isControllerEnabled){
        this.isControllerEnabled = isControllerEnabled;
    }

    @Override
	public void create () {

        if(isControllerEnabled){
            Graphics.DisplayMode dm = Gdx.graphics.getDesktopDisplayMode();
            Gdx.app.log("DISPLAY", " Using Controller Mode. W: " + dm.width + " H: " + dm.height + " X: " + dm.bitsPerPixel);
            Gdx.graphics.setDisplayMode(dm.width, dm.height, true);
            Gdx.graphics.setVSync(true);
            Gdx.input.setCursorCatched(true);
        }
        assetManager = new AssetManager();
        loadAssets();

        input = new InputMultiplexer();
	}



	@Override
	public void render () {

        if(assetManager.update()){
            if(!isInitialized){
                KasetagenStateUtil.setDebugMode(false);
                changeToScreen(MENU);
                isInitialized = true;
            }

            Gdx.gl.glClearColor(.16f, .14f, .13f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            super.render();
        }else{
            if(!isInitialized){
                changeToScreen(LOADING);
                isInitialized = true;
            }
            Gdx.gl.glClearColor(.16f, .14f, .13f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            super.render();
        }

	}

    public void loadAssets(){
        assetManager.load(AssetsUtil.DEFAULT_SKIN, AssetsUtil.SKIN);
        assetManager.load(AssetsUtil.ANIMATION_ATLAS, AssetsUtil.TEXTURE_ATLAS);
        assetManager.load(AssetsUtil.SPRITE_ATLAS, AssetsUtil.TEXTURE_ATLAS);

        assetManager.load(AssetsUtil.WARNING_INDICATOR, AssetsUtil.TEXTURE);

        assetManager.load(AssetsUtil.REXLIA_64, AssetsUtil.BITMAP_FONT);
        assetManager.load(AssetsUtil.REXLIA_48, AssetsUtil.BITMAP_FONT);
        assetManager.load(AssetsUtil.REXLIA_32, AssetsUtil.BITMAP_FONT);
        assetManager.load(AssetsUtil.REXLIA_24, AssetsUtil.BITMAP_FONT);
        assetManager.load(AssetsUtil.REXLIA_16, AssetsUtil.BITMAP_FONT);

        assetManager.load(AssetsUtil.EIGHT_BIT_BKG_MUSIC, AssetsUtil.MUSIC);
        assetManager.load(AssetsUtil.DISTORTION_BKG_MUSIC, AssetsUtil.MUSIC);

        assetManager.load(AssetsUtil.ZAP_SOUND, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.EXPLOSION_SOUND, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.POWER_ON_SOUND, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SCREAM, AssetsUtil.SOUND);

        assetManager.load(AssetsUtil.NOT_BAD, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.GREAT, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.AWESOME, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.AMAZING, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.BONKERS, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.RIDICULOUS, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.ATOMIC, AssetsUtil.SOUND);

    }

 //IGameProcessor

    @Override
    public String getStartScreen() {
        return MENU;
    }

    @Override
    public boolean isLoaded() {
        return assetManager.update();
    }

    @Override
    public float getLoadingProgress() {
        return assetManager.getProgress();
    }

    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void changeToScreen(String screenName) {
        if(LOADING.equalsIgnoreCase(screenName))    {
            if(loading == null){
                loading = new LoadingScreen(this);
            }

            setScreen(loading);
        }
        if(OPTIONS.equalsIgnoreCase(screenName)){

            if(options == null){
                options = new BubbleRunnerOptionsMenu(this);
            }


            input.clear();
            input.addProcessor(options);
            input.addProcessor(options.getStage());
            setScreen(options);

        }else if(MENU.equalsIgnoreCase(screenName)){
            if(menu == null){
                menu = new BubbleRunnerMenu(this);
            }


            input.clear();
            input.addProcessor(menu.getStage());
            input.addProcessor(menu);
            setScreen(menu);

        }else if(RUNNER.equalsIgnoreCase(screenName)){
            //Load the Game Screen!!
            if(runnerScreen == null){
                runnerScreen = new BubbleRunnerScreen(this);
            }

            input.clear();
            input.addProcessor(runnerScreen.getStage());
            setScreen(runnerScreen);
        }
        Gdx.input.setInputProcessor(input);
    }

    @Override
    public String getStoredString(String key) {
        Preferences preferences = Gdx.app.getPreferences(BUBBLE_RUNNER_DATA_NAME);
        String value = "";
        if(preferences.contains(key)){
            value = preferences.getString(key);
        }
        return value;
    }

    @Override
    public String getStoredString(String key, String defaultValue) {
        String value = getStoredString(key);
        if(value == null || "".equals(value.trim())){
            value = defaultValue;
        }
        return value;
    }

    @Override
    public int getStoredInt(String key) {
        Preferences preferences = Gdx.app.getPreferences(BUBBLE_RUNNER_DATA_NAME);
        int value = -1;
        if(preferences.contains(key)){
            value = preferences.getInteger(key);
        }
        return value;
    }

    @Override
    public float getStoredFloat(String key) {
        Preferences preferences = Gdx.app.getPreferences(BUBBLE_RUNNER_DATA_NAME);
        float value = -1f;
        if(preferences.contains(key)){
            value = preferences.getFloat(key);
        }
        return value;
    }

    @Override
    public void saveGameData(IDataSaver saver) {
        Preferences preferences = Gdx.app.getPreferences(BUBBLE_RUNNER_DATA_NAME);
        saver.updatePreferences(preferences);
        preferences.flush();
    }
}
