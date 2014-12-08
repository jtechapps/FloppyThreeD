package com.jtechapps.FloppyThreeD.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplet;
import com.jtechapps.FloppyThreeD.FloppyGame;

public class DesktopAdapter  extends LwjglApplet{
	private static final long serialVersionUID = 1L;
	static DesktopInterface nface = new DesktopInterface();
    public DesktopAdapter()
    {
        super(new FloppyGame(nface));
    }
}
