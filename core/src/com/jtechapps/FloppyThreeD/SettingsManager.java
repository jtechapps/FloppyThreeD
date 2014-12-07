package com.jtechapps.FloppyThreeD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SettingsManager {
	private boolean quickreset;
	private int difficulty = 1;//0 is noob; 1 is easy; 2 is medium; 3 is hard; 4 is pro
	
	public void togglequickreset(){
		quickreset ^= true;
		Preferences prefs = Gdx.app.getPreferences("FloppyThreeD");
		prefs.putBoolean("quickreset", quickreset);
		prefs.flush();
	}
	
	public boolean getquickreset(){
		Preferences prefs = Gdx.app.getPreferences("FloppyThreeD");
		return prefs.getBoolean("quickreset", quickreset);
	}
	
	public void toggledifficulty(){
		difficulty++;
		if(difficulty>4){
			difficulty = 0;
		}
		Preferences prefs = Gdx.app.getPreferences("FloppyThreeD");
		prefs.putInteger("difficulty", difficulty);
		prefs.flush();
	}
	
	/**
	 * 0 is noob; 1 is easy; 2 is medium; 3 is hard; 4 is pro
	 * @return
	 */
	public int getdifficulty(){
		Preferences prefs = Gdx.app.getPreferences("FloppyThreeD");
		return prefs.getInteger("difficulty", difficulty);
	}
}
