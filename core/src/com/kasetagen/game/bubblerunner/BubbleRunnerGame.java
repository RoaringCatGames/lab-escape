package com.kasetagen.game.bubblerunner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.kasetagen.game.bubblerunner.delegate.IGameProcessor;
import com.kasetagen.game.bubblerunner.delegate.IStageManager;
import com.kasetagen.game.bubblerunner.screen.BubbleRunnerMenu;
import com.kasetagen.game.bubblerunner.screen.BubbleRunnerScreen;
import com.kasetagen.game.bubblerunner.util.AssetsUtil;

public class BubbleRunnerGame extends Game implements IGameProcessor {

    private static final String MENU = "menu";
    private static final String RUNNER = "runner";

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
                changeToScreen(MENU);
                isInitialized = true;
            }

            Gdx.gl.glClearColor(.16f, .14f, .13f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            super.render();
        }
	}

    public void loadAssets(){
        assetManager.load(AssetsUtil.BLG_LOGO, AssetsUtil.TEXTURE);
        assetManager.load(AssetsUtil.COURIER_FONT_32, AssetsUtil.BITMAP_FONT);
        assetManager.load(AssetsUtil.ZAP_SOUND, AssetsUtil.SOUND);
        assetManager.load(AssetsUtil.BUBBLE_PARTICLE, AssetsUtil.PARTICLE);
        assetManager.load(AssetsUtil.BUBBLE_PARTICLE_IMG, AssetsUtil.TEXTURE);
    }

 //IGameProcessor
    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void changeToScreen(String screenName) {
        if(MENU.equalsIgnoreCase(screenName)){
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


}
