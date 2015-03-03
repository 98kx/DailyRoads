package com.konka.dialyroads.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.hardware.Camera;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.konka.dialyroads.R;
import com.konka.dialyroads.pojo.AppPara;
import com.konka.dialyroads.service.AccelerationService;
import com.konka.dialyroads.service.BackgroundWorkService;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.Util;

public class MyDialog {
	private Context context;

	public MyDialog(Context context) {
		this.context = context;
	}

	/**
	 * 普通dialog
	 * 
	 * @param context
	 * @param fragmentManager
	 */
	// public static void showSimpleDialog1(Context context, FragmentManager
	// fragmentManager) {
	// context.setTheme(R.style.DefaultDarkTheme);
	// SimpleDialogFragment.createBuilder(context, fragmentManager)//
	// .setTitle("提示")//
	// .setMessage("是否需要后台运行")//
	// .setPositiveButtonText("后台运行") // 确定按钮
	// .setNegativeButtonText("退出程序")// 取消按钮
	// .setRequestCode(Assist.DIALOGCODE_EXITAPPLIANCE_BACKGROUNDCONTINUE) // 监听
	// // .setCancelableOnTouchOutside(true)// 默认是true
	// .show();
	//
	// }

	/**
	 * 自定义view dialog
	 * 
	 * @param fragmentActivity
	 * @param view
	 *            填充view
	 */
//	public static void showCustomViewDialog1(FragmentActivity fragmentActivity, View view) {
//		fragmentActivity.setTheme(R.style.DefaultDarkTheme);
//		JayneHatDialogFragment.show(fragmentActivity, view);
//	}

	Dialog userDialog;

	public void showUserDialog() {
		if (userDialog == null) {
			userDialog = new Dialog(context);
			userDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			//
			// dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.panel_background));
			View mview = ((FragmentActivity) context).getLayoutInflater().inflate(R.layout.userfragment, null);
			userDialog.setContentView(mview, new LayoutParams(450, 445));
		}
		if (!userDialog.isShowing()) {
			userDialog.show();
		}
	}

	public void cancelUserDialog() {
		if (userDialog != null && userDialog.isShowing()) {
			userDialog.dismiss();
		}
	}

	static AlertDialog alertDialog;

	public static void showexitdialog(final Context context) {
		if (alertDialog == null) {
			alertDialog = new AlertDialog.Builder(context).setTitle("提示")//
					.setMessage("是否需要后台运行")//
					.setPositiveButton("后台运行", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							AppPara.getInstance().setFlashmode(Camera.Parameters.FLASH_MODE_OFF);
							boolean b = Assist.isRecording;
							Assist.isTake_Picture = false;
							Util.sendBroadcast(context, Assist.FINISH);
							Util.sendBroadcast(context, Assist.FOREGROUND_TO_BACKGROUND, b);
						}
					}).setNegativeButton("退出程序", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								Intent intent1 = new Intent(context, AccelerationService.class);
								context.stopService(intent1);
							} finally {
								try {
									Intent intent2 = new Intent(context, BackgroundWorkService.class);
									context.stopService(intent2);
								} finally {
									android.os.Process.killProcess(android.os.Process.myPid());
								}
							}
						}
					}).create();
			alertDialog.getWindow().setFlags(Assist.FLAG_HOMEKEY_DISPATCHED, Assist.FLAG_HOMEKEY_DISPATCHED);// 关键代码(监听home)
			// alertDialog.setCanceledOnTouchOutside(false);
			// alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
			// {
			// @Override
			// public boolean onKey(DialogInterface dialog, int keyCode,
			// KeyEvent event) {
			// switch (keyCode) {
			// case KeyEvent.KEYCODE_BACK:
			// return true;
			// }
			// return false;
			// }
			// });
		}
		try {
			alertDialog.show();
		} catch (Exception e) {
			alertDialog=null;
			showexitdialog(context);
		}
	}

	static Dialog dialog;

	public static void showViewDialog(Context context, View view) {
		if (dialog == null) {
			dialog = new Dialog(context);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(view);
		}
		dialog.show();
	}
}
