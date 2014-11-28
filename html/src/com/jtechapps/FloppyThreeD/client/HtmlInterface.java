package com.jtechapps.FloppyThreeD.client;

import com.jtechapps.FloppyThreeD.NativeInterface;

public class HtmlInterface implements NativeInterface {

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
