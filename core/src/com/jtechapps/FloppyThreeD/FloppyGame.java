package com.jtechapps.FloppyThreeD;

import com.badlogic.gdx.Game;
import com.jtechapps.FloppyThreeD.Screens.ClassicGameScreen;
import com.jtechapps.FloppyThreeD.Screens.MainMenuScreen;

public class FloppyGame extends Game {
	Game g = this;
	private NativeInterface nface;
	
	public FloppyGame(NativeInterface nativeInterface){
		nface = nativeInterface;
		
	}
	
	@Override
	public void create () {
		this.setScreen(new MainMenuScreen(g, nface));
	}

	@Override
	public void dispose(){
		nface.dispose();
		super.dispose();
	}
	
}
