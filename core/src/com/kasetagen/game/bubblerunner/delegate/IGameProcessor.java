package com.kasetagen.game.bubblerunner.delegate;

import com.badlogic.gdx.assets.AssetManager;
import com.kasetagen.game.bubblerunner.data.IDataSaver;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/26/14
 * Time: 1:04 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IGameProcessor {

    public boolean isLoaded();
    public float getLoadingProgress();
    public AssetManager getAssetManager();
    public void changeToScreen(String screenName);
    public String getStoredString(String key);
    public String getStoredString(String key, String defaultValue);
    public int getStoredInt(String key);
    public float getStoredFloat(String key);

    public void saveGameData(IDataSaver saver);
}
