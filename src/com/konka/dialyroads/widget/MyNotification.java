package com.konka.dialyroads.widget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.widget.RemoteViews;

import com.konka.dialyroads.R;
import com.konka.dialyroads.myinterface.MyNotificationInterface;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.Util;

public class MyNotification implements MyNotificationInterface {
	private Notification mNotification = null;
	private NotificationManager mNotificationManager = null;
	// private PendingIntent mPendingIntent = null;
	private static final int NOTIFICATION = 0;
	private Context context;
	private boolean isshow;

	public MyNotification(Context context) {
		this.context = context;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Assist.CRASH);
		intentFilter.addAction(Assist.KEEP);
		intentFilter.addAction(Assist.SWITCH);
		intentFilter.addAction(Assist.TIMER);// 计时器
		context.registerReceiver(receiver, intentFilter);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, Intent intent) {
			String action = intent.getAction();
			if (Assist.CRASH.equals(action)) {
				if (Assist.isRecording) {
					Assist.crash = true;
					Assist.foreverSave = true;
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							Util.sendBroadcast(context, Assist.isBackgroundWork ? Assist.STOP_VIDEO : Assist.CRASH_STOP_RECORD);
						}
					}, 3000);
				}
			} else if (Assist.KEEP.equals(action)) {
				if (Assist.isRecording) {
					Assist.foreverSave = true;
				}
			} else if (Assist.SWITCH.equals(action)) {
				Util.sendBroadcast(context, Assist.isRecording ? Assist.STOP_VIDEO : Assist.MAKE_VIDEO);
				mNotification.contentView.setTextViewCompoundDrawables(R.id.noti_switch, R.drawable.playback_start, 0, 0, 0);
				mNotification.contentView.setTextViewText(R.id.noti_switch, context.getText(R.string.start_switch));
				mNotificationManager.notify(NOTIFICATION, mNotification);
			} else if (Assist.TIMER.equals(action)) {
				String text = intent.getExtras().getString(Assist.TIMER);
				if (text != null && text != "") {
					updata(text);
				}
				mNotification.contentView.setTextViewCompoundDrawables(R.id.noti_switch, Assist.isRecording ? R.drawable.ic_small_stop : R.drawable.playback_start, 0, 0, 0);
				mNotification.contentView.setTextViewText(R.id.noti_switch, context.getText(Assist.isRecording ? R.string.noti_switch : R.string.start_switch));
				mNotificationManager.notify(NOTIFICATION, mNotification);
			}
		}
	};
	int i = 0;

	@Override
	public void showNotification() {
		mNotification = new Notification();
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mPendingIntent = PendingIntent.getActivity(context, 0, new
		// Intent(context, MainActivity.class), 0);
		showNotification1(context);
	}

	@Override
	public void cancelNotification() {
		if (mNotificationManager != null) {
			mNotificationManager.cancel(NOTIFICATION);
			mNotificationManager = null;
			mNotification = null;
			context.unregisterReceiver(receiver);
			isshow = false;
			// Toast.makeText(context, "通知销毁", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean isShowing() {

		return isshow;
	}

	/**
	 * 服务运行的时显示一个通知
	 */
	private void showNotification1(Context mContext) {
		// text显示的文字内容
		CharSequence text = mContext.getText(R.string.local_service_started);
		mNotification.icon = R.drawable.noti;
		mNotification.tickerText = text;
		// mNotification.contentIntent = mPendingIntent;
		mNotification.flags |= Notification.FLAG_NO_CLEAR;
		mNotification.contentView = new RemoteViews(mContext.getPackageName(), R.layout.notification_layout);
		// 当点击R.id.crash按钮时，发送广播 CRASH
		PendingIntent pIntentCrash = PendingIntent.getBroadcast(mContext, 0, new Intent(Assist.CRASH), 0);
		PendingIntent pIntentKeep = PendingIntent.getBroadcast(mContext, 0, new Intent(Assist.KEEP), 0);
		PendingIntent pIntentSwitch = PendingIntent.getBroadcast(mContext, 0, new Intent(Assist.SWITCH), 0);

		mNotification.contentView.setOnClickPendingIntent(R.id.crash, pIntentCrash);
		mNotification.contentView.setOnClickPendingIntent(R.id.keep, pIntentKeep);

		mNotification.contentView.setTextViewCompoundDrawables(R.id.noti_switch, Assist.isRecording ? R.drawable.ic_small_stop : R.drawable.playback_start, 0, 0, 0);
		mNotification.contentView.setTextViewText(R.id.noti_switch, mContext.getText(Assist.isRecording ? R.string.noti_switch : R.string.start_switch));

		mNotification.contentView.setOnClickPendingIntent(R.id.noti_switch, pIntentSwitch);
		mNotificationManager.notify(NOTIFICATION, mNotification);
		isshow = true;
	}

	/**
	 * 计时器
	 * 
	 * @param text
	 */
	void updata(CharSequence text) {
		mNotification.contentView.setTextViewText(R.id.elapse, text);
	}

}
