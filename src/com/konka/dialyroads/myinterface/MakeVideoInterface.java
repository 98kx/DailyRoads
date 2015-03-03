package com.konka.dialyroads.myinterface;

public interface MakeVideoInterface {
	/**
	 * 开始录像
	 */
	void startRecording();

	/**
	 * 停止录像
	 */
	void stopRecording();

	/**
	 * 释放资源
	 */
	void release();
}
