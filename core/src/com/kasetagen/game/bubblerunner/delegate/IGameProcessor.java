package com.kasetagen.game.bubblerunner.delegate;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

/**
 * Created with IntelliJ IDEA.
 * User: barry
 * Date: 5/26/14
 * Time: 1:04 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IGameProcessor {

    public AssetManager getAssetManager();
    public void changeToScreen(String screenName);
}
