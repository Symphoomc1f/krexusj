package com.java110.things.sip.remux;


import com.java110.things.sip.callback.OnProcessListener;

public abstract class Observer  extends Thread{

	public OnProcessListener onProcessListener = null;

	public abstract void onMediaStream(byte[] data,int offset,int length,boolean isAudio);

	public abstract void onPts(long pts,boolean isAudio); 

	public abstract void startRemux();

	public abstract void stopRemux();

	public void setOnProcessListener(OnProcessListener onProcessListener){
		this.onProcessListener = onProcessListener;
	}
}
