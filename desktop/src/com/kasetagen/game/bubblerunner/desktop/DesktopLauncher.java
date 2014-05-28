package com.kasetagen.game.bubblerunner.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1028;
        config.height = 480;
		new LwjglApplication(new BubbleRunnerGame(), config);
	}
}
