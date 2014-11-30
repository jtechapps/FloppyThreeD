package com.jtechapps.FloppyThreeD;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class IOSInterface implements NativeInterface {
	private AssetManager am = null;
	private BitmapFont font1 = null;

	@Override
	public void garbagecollect() {
		for(int i = 0; i <10; i++){
			Runtime.getRuntime().gc();
		}
	}

	@Override
	public void dispose() {
		if(am!=null)
			am.dispose();
		if(font1!=null)
			font1.dispose();
	}
	
	@Override
	public void setAssetManger(AssetManager assetmanager) {
		am = assetmanager;
	}

	@Override
	public AssetManager getAssetManger() {
		return am;
	}
	
	@Override
	public void setFont1(BitmapFont font) {
		font1 = font;
	}

	@Override
	public BitmapFont getFont1() {
		return font1;
	}

}
