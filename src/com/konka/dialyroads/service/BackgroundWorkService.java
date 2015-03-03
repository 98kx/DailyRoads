package com.konka.dialyroads.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.konka.dialyroads.R;
import com.konka.dialyroads.activity.MainActivity;
import com.konka.dialyroads.myinterface.BackgroundMakeVideoInterface;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.MyToast;
import com.konka.dialyroads.view.Preview;
import com.konka.dialyroads.widget.MyNotification;

/**
 * 后台录像服务
 * 
 * @author cgp
 */
public class BackgroundWorkService extends Service implements BackgroundMakeVideoInterface {

	private Preview preview;
	private Context context = this;
	private WindowManager windowManager;
	private LinearLayout relLay;
	private WindowManager.LayoutParams params1;
	private MyNotification myNotification;
	private Intent intent;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// MyToast.show(this, "后台服务已启动");
		IntentFilter filter = new IntentFilter();
		filter.addAction(Assist.MAKE_VIDEO);
		filter.addAction(Assist.STOP_VIDEO);
		filter.addAction(Assist.FOREGROUND_TO_BACKGROUND);
		filter.addAction(Assist.BACKGROUND_TO_FOREGROUND);
		filter.addAction(Assist.TOAST);
		registerReceiver(mReceiver, filter);

		intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);

		if (myNotification != null && myNotification.isShowing()) {
			myNotification.cancelNotification();
		}

	};

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Assist.FOREGROUND_TO_BACKGROUND.equals(action)) {
				Boolean b = intent.getExtras().getBoolean(Assist.CONTINUE_RECORD);
				BackgroundWorkService.this.foregroundToBackground(b);
			} else if (Assist.BACKGROUND_TO_FOREGROUND.equals(action)) {
				BackgroundWorkService.this.backgroundToForeground();
			} else if (Assist.STOP_VIDEO.equals(action)) {
				BackgroundWorkService.this.stopVideo();
			} else if (Assist.MAKE_VIDEO.equals(action)) {
				BackgroundWorkService.this.makeVideo();
			} else if (Assist.TOAST.equals(action)) {
				String text = intent.getExtras().getString(Assist.TOAST);
				MyToast.show(context, text);
			}
		}
	};
	int i = 0;

	@Override
	public void foregroundToBackground(final Boolean continue_Record) {
		Assist.isBackgroundWork = true;

		createNetTraffic();

		// 这里要等相机完全打开后才能开始录像，不然camer是null
		new Thread() {
			@Override
			public void run() {
				try {
					while (preview.getmCamera() == null) {
						Thread.sleep(1000);
						System.gc();
					}
					if (continue_Record)
						makeVideo();
					myNotification = new MyNotification(context);
					myNotification.showNotification();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();

	}

	@Override
	public void backgroundToForeground() {
		try {
			removeNetTraffic();
			if (myNotification != null) {
				myNotification.cancelNotification();
			}

			Assist.isBackgroundWork = false;
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void makeVideo() {
		if (preview.getmCamera() != null) {
			try {
				preview.startRecording();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stopVideo() {
		if (Assist.isBackgroundWork && preview != null) {
			preview.stopRecording();
		}
	}

	/**
	 * 创建悬浮窗，录像界面
	 */
	private void createNetTraffic() {
		windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		LayoutParams params = new LayoutParams();
		params.width = 1;
		params.height = 1;
		View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.preview, null);
		preview = (Preview) view.findViewById(R.id.preview);
		preview.setLayoutParams(params);
		LayoutParams params_rel = new LayoutParams();
		relLay = new LinearLayout(context);
		relLay.setLayoutParams(params_rel);
		relLay.addView(view);// -----------
		// relLay.addView(preview);
		params1 = new WindowManager.LayoutParams();
		// 设置window type
		params1.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		params1.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		params1.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		params1.x = 1;
		params1.y = 1;
		// 设置悬浮窗的长得宽
		params1.width = 1;
		params1.height = 1;
		windowManager.addView(relLay, params1); // 创建View
	}

	/**
	 * 移除悬浮窗
	 */
	private void removeNetTraffic() {
		preview.onPause();
		relLay.removeAllViews();
		windowManager.removeView(relLay);
		windowManager = null;
	}
}
