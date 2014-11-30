package com.jtechapps.FloppyThreeD;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public interface NativeInterface {
	public void garbagecollect();
	public void dispose();
	public void setAssetManger(AssetManager assetmanager);
	public AssetManager getAssetManger();
	public void setFont1(BitmapFont font);
	public BitmapFont getFont1();
}
