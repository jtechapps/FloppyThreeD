package com.jtechapps.FloppyThreeD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class ScoreManager {
	/**
	 * Compares the players score and if the new score is higher then it will set the new high score.
	 * @param score score to compare to high score.
	 * @return true if score is a new highscore
	 */
	public boolean compareScore(int score){
		if(score>getHighScore()){
			setHighScore(score);
			return true;
		}
		else {
			return false;
		}
	}
	
	public int getHighScore(){
		Preferences prefs = Gdx.app.getPreferences("FloppyThreeD");
		int highscore = prefs.getInteger("highscore", 0);
		return highscore;
	}
	
	public void setHighScore(int scoretoset){
		Preferences prefs = Gdx.app.getPreferences("FloppyThreeD");
		prefs.putInteger("highscore", scoretoset);
		prefs.flush();
	}
}
