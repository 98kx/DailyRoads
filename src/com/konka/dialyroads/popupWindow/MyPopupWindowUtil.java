package com.konka.dialyroads.popupWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.konka.dialyroads.R;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.Util;

public class MyPopupWindowUtil {
	private PopupWindow videoMenu;
	private PopupWindow imageMenu;
	private View picsetView;
	private View videosetView;
	private Context context;
	private View showView;// 显示到哪个view上面

	public MyPopupWindowUtil(Context context, View showView) {
		this.context = context;
		this.showView = showView;

		videosetView = ((Activity) context).getLayoutInflater().inflate(R.layout.video_setting_panel, null);
		videoMenu = new PopupWindow(videosetView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		videoMenu.setBackgroundDrawable(new BitmapDrawable(context.getResources()));
		videoMenu.setOutsideTouchable(true);
		videoMenu.setFocusable(true);

		picsetView = ((Activity) context).getLayoutInflater().inflate(R.layout.camera_setting_panel, null);
		imageMenu = new PopupWindow(picsetView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		imageMenu.setBackgroundDrawable(new BitmapDrawable(context.getResources())); // 点击外面让PopupWindow消失
		imageMenu.setOutsideTouchable(true);
		imageMenu.setFocusable(true);

	}

	public void showPopupWindow() {
		try {
			(Assist.isTake_Picture ? imageMenu : videoMenu).showAtLocation(showView, Gravity.RIGHT, Util.getWidth(((Activity) context).findViewById(R.id.title)), 0);
		} catch (Exception e) {
			e.printStackTrace();
			dismiss();
		}
	}

	public boolean isShowing() {
		if (videoMenu != null && videoMenu.isShowing()) {
			return true;
		}
		if (imageMenu != null && imageMenu.isShowing()) {
			return true;
		}
		return false;
	}

	public void dismiss() {
		if (videoMenu.isShowing())
			videoMenu.dismiss();
		if (imageMenu.isShowing())
			imageMenu.dismiss();
	}

	public void setOnDismissListener(OnDismissListener onDismissListener) {
		videoMenu.setOnDismissListener(onDismissListener);
		imageMenu.setOnDismissListener(onDismissListener);
	}
}
