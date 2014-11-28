package com.jtechapps.FloppyThreeD.desktop;

import com.jtechapps.FloppyThreeD.NativeInterface;

public class DesktopInterface implements NativeInterface {

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
