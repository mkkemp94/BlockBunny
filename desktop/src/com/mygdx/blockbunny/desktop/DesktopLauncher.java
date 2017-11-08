package com.mygdx.blockbunny.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.blockbunny.BlockBunny;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = BlockBunny.TITLE;
		config.width = BlockBunny.V_WIDTH * BlockBunny.SCALE;
		config.height = BlockBunny.V_HEIGHT * BlockBunny.SCALE;

		new LwjglApplication(new BlockBunny(), config);
	}
}
