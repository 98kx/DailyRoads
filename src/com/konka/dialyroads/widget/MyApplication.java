package com.konka.dialyroads.widget;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.konka.dialyroads.dao.ImageDBHelper;
import com.konka.dialyroads.dao.VideoDBHelper;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.MyFileUtils;
import com.konka.dialyroads.util.Util;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MyApplication extends Application {
	private int screenWidth;
	private int screenHeight;
	public static final String BASE_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory() + "/konka";// 文件存储目录
	public static final String VIDEOFILE_STORAGE_DIRECTORY = BASE_STORAGE_DIRECTORY + "/video/";// 视频
	public static final String IMAGEFILE_STORAGE_DIRECTORY = BASE_STORAGE_DIRECTORY + "/image/";// 图片
	public static final String THUMBFILE_STORAGE_DIRECTORY = BASE_STORAGE_DIRECTORY + "/thumb/";// 缩略图
																								// 缓存
	private static MyApplication mApplication;

	/** OPlayer SD卡缓存路径 */
	public static final String OPLAYER_CACHE_BASE = Environment.getExternalStorageDirectory() + "/oplayer";
	/** 视频截图缓冲路径 */
	public static final String OPLAYER_VIDEO_THUMB = OPLAYER_CACHE_BASE + "/thumb/";
	/** 首次扫描 */
	public static final String PREF_KEY_FIRST = "application_first";

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		init(getApplicationContext());

		mApplication = this;

//		init();
	}

//	private void init() {
//		// 创建缓存目录
//		FileUtils.createIfNoExists(OPLAYER_CACHE_BASE);
//		FileUtils.createIfNoExists(OPLAYER_VIDEO_THUMB);
//	}

	private void init(Context context) {
		initImageLoader(getApplicationContext());
		Util.initAppPara(getApplicationContext());

		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		Assist.screenWidth = dm.widthPixels;
		Assist.screenHeight = dm.heightPixels;
		/**
		 * 创建目录
		 */
		// File_Storage_Manage.create_File_Storage_Directory();
		// File_Storage_Manage.create_PicFile_Storage_Directory();
		// File_Storage_Manage.create_VideoFile_Storage_Directory();

		MyFileUtils.createIfNoExists(BASE_STORAGE_DIRECTORY);
		MyFileUtils.createIfNoExists(VIDEOFILE_STORAGE_DIRECTORY);
		MyFileUtils.createIfNoExists(IMAGEFILE_STORAGE_DIRECTORY);
		MyFileUtils.createIfNoExists(THUMBFILE_STORAGE_DIRECTORY);

		Assist.videoDBHelper = new VideoDBHelper(context);
		Assist.imageDBHelper = new ImageDBHelper(context);
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)//
				.threadPriority(Thread.NORM_PRIORITY - 2)//
				.denyCacheImageMultipleSizesInMemory()//
				.discCacheFileNameGenerator(new Md5FileNameGenerator())//
				.tasksProcessingOrder(QueueProcessingType.LIFO)//
				// .writeDebugLogs() // Remove
				// for
				// release
				// app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);

	}

	public static MyApplication getApplication() {
		return mApplication;
	}

	public static Context getContext() {
		return mApplication;
	}

	/** 销毁 */
	public void destory() {
		mApplication = null;
	}
}