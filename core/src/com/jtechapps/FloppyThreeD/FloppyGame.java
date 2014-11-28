package com.jtechapps.FloppyThreeD;

import com.badlogic.gdx.Game;
import com.jtechapps.FloppyThreeD.Screens.ClassicGameScreen;

public class FloppyGame extends Game {
	Game g = this;
	
	@Override
	public void create () {
		this.setScreen(new ClassicGameScreen(g));
	}

	@Override
	public void dispose(){
		super.dispose();
	}
	
}
