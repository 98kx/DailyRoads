package com.konka.dialyroads.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;

import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.MyToast;
import com.konka.dialyroads.util.Util;

public class AccelerationService extends Service implements SensorEventListener {
	SensorManager sm = null;

	@Override
	public void onCreate() {

		super.onCreate();
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		// MyToast.show(this, "监听碰撞的服务已启动");
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

		// new Timer().schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		// ActivityManager am = (ActivityManager)
		// getSystemService(ACTIVITY_SERVICE);
		// ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		// String packageName = cn.getPackageName();
		//
		// System.out.println("packageName"+packageName);
		// }
		// },0, 1000);

		return START_STICKY;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float[] f = event.values;

			if (f[0] * f[0] + f[1] * f[1] + f[2] * f[2] > 20 * 20) {
				// System.out.println(f[0] * f[0] + f[1] * f[1] + f[2] * f[2]);
				if (Assist.isRecording) {
					Assist.crash = true;
					Assist.foreverSave = true;
					// MovieRecorder.keep = 1;
//					MyToast.show(getApplicationContext(), "现在录制的画面,将保存为事故录像");
					Util.sendBroadcast(getApplicationContext(), Assist.TOAST, "现在录制的画面,将保存为事故录像");
					/**
					 * 延时5秒
					 */
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if (Assist.isRecording) {
								Util.sendBroadcast(AccelerationService.this, Assist.isBackgroundWork ? Assist.STOP_VIDEO : Assist.CRASH_STOP_RECORD);
							}
						}
					}, 3000);
					// Util.send(this, Assist.SHOW_SOS);
				}

			}
		}
	}

	@Override
	public void onDestroy() {
		sm.unregisterListener(this);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// MyToast.show(this, "监听碰撞的服务绑定成功");
		return null;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

}
