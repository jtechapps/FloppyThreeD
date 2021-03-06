package com.jtechapps.FloppyThreeD.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jtechapps.FloppyThreeD.FloppyGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		DesktopInterface nface = new DesktopInterface();
		new LwjglApplication(new FloppyGame(nface), config);
	}
}
