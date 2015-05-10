package com.kasetagen.game.bubblerunner;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.kasetagen.engine.IDataSaver;
import com.kasetagen.engine.IGameProcessor;
import com.kasetagen.engine.gdx.scenes.scene2d.KasetagenStateUtil;
import com.kasetagen.engine.screen.LoadingScreen;
import com.kasetagen.game.bubblerunner.data.GameOptions;
import com.kasetagen.game.bubblerunner.scene2d.BaseStage;
import com.kasetagen.game.bubblerunner.screen.BubbleRunnerMenu;
import com.kasetagen.game.bubblerunner.screen.BubbleRunnerScreen;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;

public class BubbleRunnerGame extends Game implements IGameProcessor {

    public static final String LOADING = "loading";
    public static final String MENU = "menu";
    public static final String RUNNER = "runner";

    public static final String BUBBLE_RUNNER_DATA_NAME = "BubbleRunnerData";

    LoadingScreen loading;
    BubbleRunnerMenu menu;
    BubbleRunnerScreen runnerScreen;
    InputMultiplexer input;

    Music bgMusic;

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
        Gdx.input.setCatchBackKey(true);
        assetManager = new AssetManager();
        loadAssets();
        float val = getStoredFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY);
        if(val < 0f){
               saveGameData(new IDataSaver() {
                   @Override
                   public void updatePreferences(Preferences preferences) {
                       preferences.putFloat(GameOptions.BG_MUSIC_VOLUME_PREF_KEY, 0.5f);
                   }
               });
        }

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

        assetManager.load(AssetsUtil.NEUROPOL_32, AssetsUtil.BITMAP_FONT);
        assetManager.load(AssetsUtil.NEUROPOL_48, AssetsUtil.BITMAP_FONT);
        assetManager.load(AssetsUtil.NEUROPOL_64, AssetsUtil.BITMAP_FONT);

        assetManager.load(AssetsUtil.MENU_BG_MUSIC, AssetsUtil.MUSIC);
        assetManager.load(AssetsUtil.GAME_BG_MUSIC, AssetsUtil.MUSIC);

        assetManager.load(AssetsUtil.SND_SHIELD_ON, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SND_SHIELD_WRONG, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SND_WB_ELECTRIC, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SND_WB_GLASS, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SND_WB_LASER, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SND_DEATH_THUD, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SND_DEATH_EDYN, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SND_DEATH_EDISON, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SND_DEATH_SHOCK, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SND_DEATH_FIRE, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SND_SICK_EDYN, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SND_SICK_EDISON, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SND_EDYN_READY, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.SND_EDISON_READY, AssetsUtil.SOUND);

        assetManager.load(AssetsUtil.NOT_BAD, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.GREAT, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.AWESOME, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.AMAZING, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.PERFECT, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.UNBELIEVABLE, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.UNSTOPPABLE, AssetsUtil.SOUND);

    }

 //IGameProcessor


    @Override
    public void setBGMusic(Music music) {
        if(music != null){
            bgMusic = music;
        }
    }

    @Override
    public void setBGMusicVolume(float newVolume) {
        if(bgMusic != null){
            bgMusic.setVolume(newVolume);
        }
    }

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
                loading = new LoadingScreen(this, "animations/loading.atlas", "Lab_Loading", new BaseStage(this));
                loading.setIsAnimatedWithProgress(false);
            }

            setScreen(loading);
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
            input.addProcessor(runnerScreen);
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
