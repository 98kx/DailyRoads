package com.konka.dialyroads.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.konka.dialyroads.R;
import com.konka.dialyroads.util.Assist;

public class ShowOneImageFileActivity extends Activity {
	ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.ac_image_pager);

		String url = getIntent().getExtras().getString("url");
		imageView = (ImageView) findViewById(R.id.pager);
		if (url.startsWith("http")) {
			Assist.imageLoader.displayImage(url, imageView);
		} else {
			Assist.imageLoader.displayImage("file:///" + url, imageView);
		}
	}

}
