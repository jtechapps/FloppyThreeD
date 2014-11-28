package com.jtechapps.FloppyThreeD;

public class IOSInterface implements NativeInterface {

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
