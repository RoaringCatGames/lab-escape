package com.kasetagen.game.bubblerunner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.kasetagen.engine.gdx.scenes.scene2d.KasetagenStateUtil;
import com.kasetagen.game.bubblerunner.data.GameStats;
import com.kasetagen.game.bubblerunner.data.IDataSaver;
import com.kasetagen.game.bubblerunner.delegate.IGameProcessor;
import com.kasetagen.game.bubblerunner.screen.BubbleRunnerMenu;
import com.kasetagen.game.bubblerunner.screen.BubbleRunnerOptionsMenu;
import com.kasetagen.game.bubblerunner.screen.BubbleRunnerScreen;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;

import java.util.HashMap;
import java.util.Map;

public class BubbleRunnerGame extends Game implements IGameProcessor {

    public static final String OPTIONS = "options";
    public static final String MENU = "menu";
    public static final String RUNNER = "runner";

    public static final String BUBBLE_RUNNER_DATA_NAME = "BubbleRunnerData";

    BubbleRunnerOptionsMenu options;
    BubbleRunnerMenu menu;
    BubbleRunnerScreen runnerScreen;

    boolean isInitialized = false;

    protected AssetManager assetManager;

	@Override
	public void create () {
        assetManager = new AssetManager();
        loadAssets();
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
        }
	}

    public void loadAssets(){
        assetManager.load(AssetsUtil.TITLE_SCREEN, AssetsUtil.TEXTURE);

        assetManager.load(AssetsUtil.DEFAULT_SKIN, AssetsUtil.SKIN);

        assetManager.load(AssetsUtil.COURIER_FONT_32, AssetsUtil.BITMAP_FONT);
        assetManager.load(AssetsUtil.COURIER_FONT_18, AssetsUtil.BITMAP_FONT);
        assetManager.load(AssetsUtil.COURIER_FONT_12, AssetsUtil.BITMAP_FONT);

        assetManager.load(AssetsUtil.ALT_BG_MUSIC, AssetsUtil.MUSIC);
        assetManager.load(AssetsUtil.BACKGROUND_SOUND, AssetsUtil.MUSIC);
        assetManager.load(AssetsUtil.ZAP_SOUND, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.EXPLOSION_SOUND, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.POWER_ON_SOUND, AssetsUtil.SOUND);

        assetManager.load(AssetsUtil.BUBBLE_PARTICLE, AssetsUtil.PARTICLE);
        assetManager.load(AssetsUtil.BUBBLE_PARTICLE_IMG, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.PLAYER_IMG, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.ANIMATION_ATLAS, AssetsUtil.TEXTURE_ATLAS);

        assetManager.load(AssetsUtil.PLAYER_IMG, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.INDICATOR_SHEET, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.LIGHTNING_WALL, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.PLASMA_WALL, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.LASER_WALL, AssetsUtil.TEXTURE);

        assetManager.load(AssetsUtil.LIGHT_UP, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.LIGHT_DOWN, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.LIGHT_CHECKED, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.PLASMA_UP, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.PLASMA_DOWN, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.PLASMA_CHECKED, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.LASER_UP, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.LASER_DOWN, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.LASER_CHECKED, AssetsUtil.TEXTURE);
        
        assetManager.load(AssetsUtil.FLOOR_CONC, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.FLOOR_PILLAR, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.WALL, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.BACKGROUND, AssetsUtil.TEXTURE);
        
        assetManager.load(AssetsUtil.ENERGY_BAR, AssetsUtil.TEXTURE);

    }

 //IGameProcessor
    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void changeToScreen(String screenName) {
        if(OPTIONS.equalsIgnoreCase(screenName)){

            if(options == null){
                options = new BubbleRunnerOptionsMenu(this);
            }

            setScreen(options);
            Gdx.input.setInputProcessor(options.getStage());

        }else if(MENU.equalsIgnoreCase(screenName)){
            if(menu == null){
                menu = new BubbleRunnerMenu(this);
            }

            setScreen(menu);
            Gdx.input.setInputProcessor(menu.getStage());

        }else if(RUNNER.equalsIgnoreCase(screenName)){
            //Load the Game Screen!!
            if(runnerScreen == null){
                runnerScreen = new BubbleRunnerScreen(this);
            }

            setScreen(runnerScreen);
            Gdx.input.setInputProcessor(runnerScreen.getStage());
        }
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
