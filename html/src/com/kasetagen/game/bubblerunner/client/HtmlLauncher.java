package com.kasetagen.game.bubblerunner.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(1028, 580);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new BubbleRunnerGame();
        }
}