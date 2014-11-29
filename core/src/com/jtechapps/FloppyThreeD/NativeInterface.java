package com.jtechapps.FloppyThreeD;

import com.badlogic.gdx.assets.AssetManager;

public interface NativeInterface {
	public void garbagecollect();
	public void dispose();
	public void setAssetManger(AssetManager assetmanager);
	public AssetManager getAssetManger();
}
