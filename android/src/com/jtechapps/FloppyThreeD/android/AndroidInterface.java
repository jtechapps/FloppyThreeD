package com.jtechapps.FloppyThreeD.android;

import com.jtechapps.FloppyThreeD.NativeInterface;

public class AndroidInterface implements NativeInterface {

	@Override
	public void garbagecollect() {
		for(int i = 0; i <10; i++){
			Runtime.getRuntime().gc();
		}
	}

	@Override
	public void dispose() {
		
	}

}
