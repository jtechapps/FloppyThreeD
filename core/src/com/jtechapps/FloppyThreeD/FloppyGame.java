package com.jtechapps.FloppyThreeD;

import com.badlogic.gdx.Game;
import com.jtechapps.FloppyThreeD.Screens.ClassicGameScreen;

public class FloppyGame extends Game {
	Game g = this;
	private NativeInterface nface;
	
	public FloppyGame(NativeInterface nativeInterface){
		nface = nativeInterface;
		
	}
	
	@Override
	public void create () {
		this.setScreen(new ClassicGameScreen(g, nface));
	}

	@Override
	public void dispose(){
		super.dispose();
	}
	
}
