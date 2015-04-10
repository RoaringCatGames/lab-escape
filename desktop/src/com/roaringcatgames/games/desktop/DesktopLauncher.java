package com.roaringcatgames.games.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kasetagen.game.bubblerunner.BubbleRunnerGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Lab Escape";
        config.width = 1280;
        config.height = 720;
        config.addIcon("icon_256x256.png", Files.FileType.Internal);
        config.addIcon("icon_64x64.png", Files.FileType.Internal);
        config.addIcon("icon_32x32.png", Files.FileType.Internal);

        boolean useController = true;
		new LwjglApplication(new BubbleRunnerGame(useController), config);
	}
}
