package com.konka.dialyroads.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * 功能：监听网络变化 作者： 黄辉 时间：2013-12-2下午4:26:39 包名：com.hh.user
 */
public class NetworkConnectionDetection {

	// @Override
	// public void onReceive(Context context, Intent intent) {
	// // TODO Auto-generated method stub
	// ConnectivityManager connectMgr = (ConnectivityManager)
	// context.getSystemService(Context.CONNECTIVITY_SERVICE);
	// NetworkInfo mobNetInfo =
	// connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	// NetworkInfo wifiNetInfo =
	// connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	// NetworkInfo info = connectMgr.getActiveNetworkInfo();
	// if (mobNetInfo.isConnected() || wifiNetInfo.isConnected()) {
	// Log.v("hh", "connect");
	// Toast.makeText(context, "网络链接上了", Toast.LENGTH_LONG).show();
	// }else {
	// Log.v("hh", "unconnect");
	// Toast.makeText(context, "无网络链接", Toast.LENGTH_LONG).show();
	// }
	// }

	// 获取当前网络连接的类型信息
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	// 判断MOBILE网络是否可用
	public boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// 判断WIFI网络是否可用
	public boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// 判断是否有网络连接
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
}
/*
 * ///////////////////////
 * 
 * <receiver android:name=".NetBroadcastReceiver"> <intent-filter > <action
 * android:name="android.net.conn.CONNECTIVITY_CHANGE" /> </intent-filter>
 * </receiver> /////////////////////////////////
 * 
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 */