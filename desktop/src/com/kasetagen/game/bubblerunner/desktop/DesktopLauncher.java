package com.kasetagen.game.bubblerunner.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Bubble Runner";
        config.width = 1280;
        config.height = 720;

        boolean useController = true;
		new LwjglApplication(new BubbleRunnerGame(useController), config);
	}
}
