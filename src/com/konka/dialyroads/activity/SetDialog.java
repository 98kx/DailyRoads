package com.konka.dialyroads.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import com.konka.dialyroads.R;

public class SetDialog extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 设置横屏显示
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.webfile);
	}
}
