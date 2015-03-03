package com.konka.dialyroads.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.konka.dialyroads.R;
import com.konka.dialyroads.dialog.MyDialog;
import com.konka.dialyroads.pojo.AppPara;
import com.konka.dialyroads.pojo.ImageFileBean;
import com.konka.dialyroads.pojo.VideoFileBean;
import com.konka.dialyroads.popupWindow.MyPopupWindowUtil;
import com.konka.dialyroads.service.AccelerationService;
import com.konka.dialyroads.service.BackgroundWorkService;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.MyFileUtils;
import com.konka.dialyroads.util.Util;
import com.konka.dialyroads.view.Preview;

public class MainActivity extends FragmentActivity {
	private Context context = this;
	private Preview preview = null;;// 拍照 录像的操作都放在这里
	private TextView time_fen_textview;
	private ImageView video_button_icon;
	private ImageView camera_button_icon;
	private AnimationDrawable animation_drawable;
	private long start_click = System.currentTimeMillis();
	private long end_click = 0;
	private MyPopupWindowUtil myPopupWindowUtil;
	private ImageView ctl_btn_menu;
	private ImageView review_icon;
	private final String FILETYPE = "filetype";
	private final String FILEPATH = "filepath";

	private TextView flash_auto;
	private TextView flash_open;
	private TextView flash_close;
	private ImageView flash_image1;
	private ImageView flash_image2;

	private Intent backgroundWorkService;
	private Intent accelerationService;

	private View flash_layout;
	private boolean startflash = true;// 相机拍照闪光控制

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().setFlags(Assist.FLAG_HOMEKEY_DISPATCHED, Assist.FLAG_HOMEKEY_DISPATCHED);// 关键代码(监听home)

		setContentView(R.layout.main1);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		preview = (Preview) findViewById(R.id.preview);
		time_fen_textview = (TextView) findViewById(R.id.time_fen_textview);
		video_button_icon = (ImageView) findViewById(R.id.video_button_icon);
		camera_button_icon = (ImageView) findViewById(R.id.camera_button_icon);
		video_button_icon.setVisibility(Assist.isTake_Picture ? View.GONE : View.VISIBLE);
		camera_button_icon.setVisibility(Assist.isTake_Picture ? View.VISIBLE : View.GONE);
		ctl_btn_menu = (ImageView) findViewById(R.id.setting_panel_icon);
		review_icon = (ImageView) findViewById(R.id.review_icon);

		flash_auto = (TextView) findViewById(R.id.flash_auto);
		flash_auto.setTag(new String[] { Parameters.FLASH_MODE_AUTO, Parameters.FLASH_MODE_AUTO });
		flash_open = (TextView) findViewById(R.id.flash_open);
		flash_open.setTag(new String[] { Parameters.FLASH_MODE_TORCH, Parameters.FLASH_MODE_ON });
		flash_close = (TextView) findViewById(R.id.flash_close);
		flash_close.setTag(new String[] { Parameters.FLASH_MODE_OFF, Parameters.FLASH_MODE_OFF });

		flash_image1 = (ImageView) findViewById(R.id.flash_image1);
		flash_image2 = (ImageView) findViewById(R.id.flash_image2);
		flash_layout = findViewById(R.id.flash_layout);

		initReceiver();
		startserivice();
		showVideoIcon();
	}

	/**
	 * 启动服务
	 */
	void startserivice() {
		backgroundWorkService = new Intent(this, BackgroundWorkService.class);
		accelerationService = new Intent(this, AccelerationService.class);
		startService(backgroundWorkService);
		startService(accelerationService);
	}

	void initReceiver() {
		/**
		 * 注册广播
		 */
		IntentFilter filter = new IntentFilter();
		filter.addAction(Assist.TIMER);// 计时器
		filter.addAction(Assist.START_RECORD);// 录像开始
		filter.addAction(Assist.STOP_RECORD);// 录像停止
		filter.addAction(Assist.STOP_TAKEPICTURE);// 停止拍照
		filter.addAction(Assist.CRASH_STOP_RECORD);// 发生碰撞，暂停录像
		filter.addAction(Assist.FINISH);// 销毁Antivity
		filter.addAction(Assist.TOAST);// toash
		registerReceiver(receiver, filter);

	}

	@Override
	protected void onResume() {// 这个方法执行的时候SurfaceView 的Callback会支持创建
		super.onResume();
		// preview.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();
		preview.onPause();
	}

	/**
	 * 菜单健
	 * 
	 * @param view
	 */
	public void menuClick(View view) {
		more(); 
	}

	private void more() {
		if (!Assist.isRecording && !Assist.isTake_Pictureing) {
			if (myPopupWindowUtil == null) {
				myPopupWindowUtil = new MyPopupWindowUtil(context, preview);
				myPopupWindowUtil.setOnDismissListener(new PopupWindow.OnDismissListener() {
					@Override
					public void onDismiss() {
						ctl_btn_menu.setImageDrawable(getResources().getDrawable(R.drawable.ctl_btn_menu_n));
					}
				});
			}
			if (!myPopupWindowUtil.isShowing()) {
				myPopupWindowUtil.showPopupWindow();
				ctl_btn_menu.setImageDrawable(getResources().getDrawable(R.drawable.ctl_btn_menu_p));
			} else {
				myPopupWindowUtil.dismiss();
				ctl_btn_menu.setImageDrawable(getResources().getDrawable(R.drawable.ctl_btn_menu_n));
			}
		}
	}

	/**
	 * 快门健
	 * 
	 * @param view
	 */
	public void shutter_onClick(View view) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(context, "使用相机前请先插入SD卡", Toast.LENGTH_LONG).show();
			return;
		}
		end_click = System.currentTimeMillis();
		if (myPopupWindowUtil != null && myPopupWindowUtil.isShowing()) {
			myPopupWindowUtil.dismiss();
			ctl_btn_menu.setImageDrawable(getResources().getDrawable(R.drawable.ctl_btn_menu_n));
		}
		if (end_click - start_click > 1000 * 0.5) {// 打开相机和关闭相机的时间较长，防止按键太快
			new Thread() {
				@Override
				public void run() {
					preview.takePicture();
				};
			}.start();
		}
		start_click = end_click;
	}

	private boolean flashisopen = false;

	/**
	 * 闪光灯
	 * 
	 * @param view
	 */
	public void onClickFlashmode(View view) {
		// Toast.makeText(context, "menuClick", Toast.LENGTH_LONG).show();
		if (!Assist.isTake_Picture) { // 不是拍照界面
			flash_layout.setVisibility(View.VISIBLE);
			if (!flashisopen) { // 选择菜单关闭的我们就打开
				openVideoFlashMenu();
			} else {
				closeVideoFlashMenu(view);

				Parameters p = preview.getmCamera().getParameters();
				AppPara.getInstance().setFlashmode(((String[]) view.getTag())[0]);
				p.setFlashMode(AppPara.getInstance().getFlashmode());
				preview.getmCamera().setParameters(p);
				preview.getmCamera().startPreview();
			}
		} else { // 拍照界面
			if (!startflash) {
				flash_layout.setVisibility(View.GONE);
			} else {
				if (!flashisopen) { // 选择菜单关闭的我们就打开
					openImageFlashMenu();
				} else {
					closeImageFlashMenu(view);

					Parameters p = preview.getmCamera().getParameters();
					AppPara.getInstance().setFlashmode(((String[]) view.getTag())[1]);
					p.setFlashMode(AppPara.getInstance().getFlashmode());
					preview.getmCamera().setParameters(p);
					preview.getmCamera().startPreview();
				}
			}
		}
	}

	private void openVideoFlashMenu() { // 录像
		flash_open.setVisibility(View.VISIBLE);
		flash_close.setVisibility(View.VISIBLE);
		flash_image2.setVisibility(View.VISIBLE);
		flashisopen = true;
	}

	private void closeVideoFlashMenu(View view) {
		flash_open.setVisibility(View.GONE);
		flash_close.setVisibility(View.GONE);
		flash_image2.setVisibility(View.GONE);
		view.setVisibility(View.VISIBLE);
		flashisopen = false;
	}

	private void openImageFlashMenu() { // 拍照
		flash_open.setVisibility(View.VISIBLE);
		flash_close.setVisibility(View.VISIBLE);
		flash_auto.setVisibility(View.VISIBLE);
		flash_image1.setVisibility(View.VISIBLE);
		flash_image2.setVisibility(View.VISIBLE);
		flashisopen = true;
	}

	private void closeImageFlashMenu(View view) {
		flash_open.setVisibility(View.GONE);
		flash_close.setVisibility(View.GONE);
		flash_auto.setVisibility(View.GONE);
		flash_image1.setVisibility(View.GONE);
		flash_image2.setVisibility(View.GONE);
		view.setVisibility(View.VISIBLE);
		flashisopen = false;
	}

	private void defaultFlashShow() {
		flash_open.setVisibility(View.GONE);
		flash_close.setVisibility(View.VISIBLE);
		flash_auto.setVisibility(View.GONE);
		flash_image1.setVisibility(View.GONE);
		flash_image2.setVisibility(View.GONE);
		Parameters ps = preview.getmCamera().getParameters();
		AppPara.getInstance().setFlashmode(Camera.Parameters.FLASH_MODE_OFF);
		ps.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		preview.getmCamera().setParameters(ps);
		preview.startCameraPreview();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Assist.TIMER.equals(action)) {// j计时器计时
				String text = intent.getExtras().getString(Assist.TIMER);
				time_fen_textview.setText(text != null ? text : "00:00");
			} else if (Assist.START_RECORD.equals(action)) {// 开始录像 开始动画
				start_animation();
			} else if (Assist.STOP_RECORD.equals(action)) { // 停止录像。停止动画
				stop_animation();
				showVideoIcon();
			} else if (Assist.FINISH.equals(action)) {
				// stopService(backgroundWorkService);
				MainActivity.this.finish();
			} else if (Assist.STOP_TAKEPICTURE.equals(action)) {
				showImageIcon();
			} else if (Assist.CRASH_STOP_RECORD.equals(action)) {
				showPopupWindow_sos();
				shutter_onClick(null);
			}

		}
	};

	/**
	 * 主界面右下角的最近一次录像和拍照小图标
	 */
	private void showVideoIcon() {
		VideoFileBean videoFileBean = Assist.videoDBHelper.getLatest();
		if (videoFileBean == null || !MyFileUtils.checkFileExists(videoFileBean.getPath()))
			return;
		MyFileUtils.checkVideoThumb(videoFileBean.getThumbpath(), videoFileBean.getPath());
		Bitmap bt = Assist.imageLoader.loadImageSync("file:///" + videoFileBean.getThumbpath());
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createScaledBitmap(bt, 60, 60, true);
			if (bitmap == null)
				return;
		} catch (Exception e) {
			return;
		}
		review_icon.setImageBitmap(bitmap);
		Map<String, String> map = new HashMap<String, String>();
		map.put(FILETYPE, Assist.VIDEO);
		map.put(FILEPATH, videoFileBean.getPath());
		review_icon.setTag(map);
	}

	/**
	 * 主界面右下角的最近一次录像和拍照小图标
	 */
	private void showImageIcon() {
		ImageFileBean imageFileBean = Assist.imageDBHelper.getLatest();
		if (imageFileBean == null || !MyFileUtils.checkFileExists(imageFileBean.getPath()))
			return;
		MyFileUtils.checkVideoThumb(imageFileBean.getThumbpath(), imageFileBean.getPath());
		Bitmap bt = Assist.imageLoader.loadImageSync("file:///" + imageFileBean.getThumbpath());
		if (bt == null) {
			return;
		}
		Bitmap bitmap = Bitmap.createScaledBitmap(bt, 60, 60, true);
		if (bitmap == null)
			return;
		review_icon.setImageBitmap(bitmap);
		Map<String, String> map = new HashMap<String, String>();
		map.put(FILETYPE, Assist.IMAGE);
		map.put(FILEPATH, imageFileBean.getPath());
		review_icon.setTag(map);
	}

	private void start_animation() {
		this.video_button_icon.setImageResource(R.drawable.frame);
		// 3. 获取动画对象
		animation_drawable = (AnimationDrawable) this.video_button_icon.getDrawable();
		animation_drawable.start();

	}

	private void stop_animation() {
		if (animation_drawable != null) {
			if (animation_drawable.isRunning()) {
				animation_drawable.stop();
				this.video_button_icon.setImageResource(R.drawable.btn_ic_video_record);
			}
		}
	}

	public void sss(String s) {
		System.out.println("执行了sssss");
	}

	public void sss() {
		System.out.println("执行了sss");
		System.out.println("dialog---" + myDialog);
	}

	public MyDialog myDialog;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			MyDialog.showexitdialog(context);
			// MyDialog.showSimpleDialog(context, getSupportFragmentManager());
			break;
		case KeyEvent.KEYCODE_HOME:
			MyDialog.showexitdialog(context);
			// MyDialog.showSimpleDialog(context, getSupportFragmentManager());
			// finish();
			break;
		case KeyEvent.KEYCODE_MENU:
			// try {
			// MyDialog.showUserDialog(this);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// showuserDialog();
			if (myDialog == null) {
				myDialog = new MyDialog(this);
			}
			myDialog.showUserDialog();

			break;

		default:
			break;
		}

		return true;
	}

	// ImageView video_button_icon;

	/**
	 * 录像切换拍照
	 */
	public void videotoCamera(View view) {
		video_button_icon.setVisibility(View.GONE);
		camera_button_icon.setVisibility(View.VISIBLE);
		Assist.isTake_Picture = true;
		more();
		showImageIcon();
		if (!startflash) {
			flash_layout.setVisibility(View.GONE);
		} else {
			defaultFlashShow();
		}
		time_fen_textview.setVisibility(View.INVISIBLE);
	}

	/**
	 * 打开本地文件
	 */
	public void openfile(View view) {
		more();
		Intent intent = new Intent(context, LocalFileActivity.class);
		startActivity(intent);

	}

	/**
	 * 打开网络文件
	 */
	public void webfile(View view) {
		more();
		if (Util.detectionNetwork(this)) {
			if (Assist.user != null) {
				Intent intent = new Intent(context, WebFileActivity.class);
				startActivity(intent);
			} else {
				Util.sendBroadcast(context, Assist.TOAST, "请先登录");
				// MyDialog.showUserDialog(context);
				if (myDialog == null) {
					myDialog = new MyDialog(this);
				}
				myDialog.showUserDialog();
			}
		}
	}

	/**
	 * 打开设置界面
	 */
	Dialog setDialog;

	public void app_setting(View view) {
		more();
		// Intent intent = new Intent(context, SetDialog.class);
		// startActivity(intent);
		if (setDialog == null) {
			setDialog = new Dialog(context);
			setDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			// dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.panel_background));
			View mview = getLayoutInflater().inflate(R.layout.setfragment, null);
			setDialog.setContentView(mview, new LayoutParams(450, 445));
			setDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					System.out.println("setOnDismissListener");
					Util.saveAppPara(AppPara.getInstance(), context);

				}
			});

		}
		setDialog.show();
	}

	/**
	 * 退出
	 */
	public void exit(View view) {
		try {
			stopService(backgroundWorkService);
		} finally {
			try {
				stopService(accelerationService);
			} finally {
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}

	}

	/**
	 * 拍照切换录像
	 */
	public void cameraToVideo(View view) {
		video_button_icon.setVisibility(View.VISIBLE);
		camera_button_icon.setVisibility(View.GONE);
		Assist.isTake_Picture = false;
		more();
		showVideoIcon();
		if (!startflash) {
			flash_layout.setVisibility(View.VISIBLE);
		}
		defaultFlashShow();
		time_fen_textview.setVisibility(View.VISIBLE);
	}

	public void review_iconClick(View view) {
		System.out.println("review_iconClick");
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) view.getTag();
		if (map == null) {
			// Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
			return;
		}
		if (Assist.IMAGE.equals(map.get(FILETYPE))) {
			Intent intent = new Intent(context, ShowOneImageFileActivity.class);
			intent.putExtra("url", map.get(FILEPATH));
			startActivity(intent);
		} else if (Assist.VIDEO.equals(map.get(FILETYPE))) {
			Util.playVideo(context, map.get(FILEPATH));
		}
	}

	/**
	 * 发生碰撞时候显示
	 */
	String phone_number;
	PopupWindow mPopupWindow_crash;

	public void showPopupWindow_sos() {
		if (mPopupWindow_crash != null && mPopupWindow_crash.isShowing()) {

		} else {
			Context mContext = MainActivity.this;
			LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
			View time_popunwindwow = mLayoutInflater.inflate(R.layout.camera_sos, null);
			phone_number = AppPara.getInstance().getTelephone();
			mPopupWindow_crash = new PopupWindow(time_popunwindwow, 150, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			mPopupWindow_crash.setFocusable(false);

			// 获取电话号码
			mPopupWindow_crash.showAtLocation(findViewById(R.id.main), Gravity.LEFT, 0, 0);
		}
	}

	public void cancel(View view) {

		mPopupWindow_crash.dismiss();

	}

	public void call(View v) {
		// MyToast.show(getApplicationContext(), "打电话");
		mPopupWindow_crash.dismiss();
		Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phone_number));
		startActivity(phoneIntent);

	}

	public void sendMessage(View v) {
		// MyToast.show(getApplicationContext(), "发信息");
		mPopupWindow_crash.dismiss();
		Uri uri = Uri.parse("smsto: " + phone_number);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", "发生碰撞,");
		startActivity(it);

	}
}
