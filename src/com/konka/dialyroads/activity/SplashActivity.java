package com.konka.dialyroads.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.konka.dialyroads.R;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.Util;

public class SplashActivity extends Activity {
	private Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.splash);
		

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		Timer timer=new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (Assist.isBackgroundWork) {// 关闭悬浮窗后去服务中启动activity
					Util.sendBroadcast(context, Assist.BACKGROUND_TO_FOREGROUND);
					SplashActivity.this.finish();
					System.out.println("111111111");
				} else {
					System.out.println("222222222");
					Intent intent = new Intent(context, MainActivity.class);
					startActivity(intent);
					SplashActivity.this.finish();
				}
				
			}
		}, 1000);
//		new Handler().postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				
//				
//			}
//		},1000);

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 退出健监听
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return true;
	}
}
