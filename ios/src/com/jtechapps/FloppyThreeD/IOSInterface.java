package com.jtechapps.FloppyThreeD;

import com.badlogic.gdx.assets.AssetManager;

public class IOSInterface implements NativeInterface {
	private AssetManager am = null;

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
	}
	
	@Override
	public void setAssetManger(AssetManager assetmanager) {
		am = assetmanager;
	}

	@Override
	public AssetManager getAssetManger() {
		return am;
	}

}
