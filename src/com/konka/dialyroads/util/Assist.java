package com.konka.dialyroads.util;

import com.konka.dialyroads.R;
import com.konka.dialyroads.dao.ImageDBHelper;
import com.konka.dialyroads.dao.VideoDBHelper;
import com.konka.dialyroads.pojo.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class Assist {
	public static int screenWidth = 0; //
	public static int screenHeight = 0;

	public static final int DIALOGCODE_EXITAPPLIANCE_BACKGROUNDCONTINUE = 40;

	public static final String IMAGE = "image";
	public static final String VIDEO = "video";

	public static final String TIMER = "timer";// 定时器
	public static final String START_RECORD = "start_record";// 录像开始
	public static final String STOP_RECORD = "stop_record"; // 录像停止
	public static final String STOP_TAKEPICTURE = "stop_akepicture"; // 录像停止
	public static final String CRASH_STOP_RECORD = "crash_stop_record";
	public static final String TOAST = "Toast";
	public static final String FINISH = "finish";
	public static final String MAKE_VIDEO = "makeVideo";
	public static final String STOP_VIDEO = "stopVideo";
	public static final String FOREGROUND_TO_BACKGROUND = "foreground_To_Background";
	public static final String BACKGROUND_TO_FOREGROUND = "background_To_Foreground";
	public static final String CRASH = "crash";
	public static final String KEEP = "keep";
	public static final String SWITCH = "switch";// 开关

	public static boolean isBackgroundWork = false;// 前台录像和后台录像的标志
	public static boolean isTake_Picture = false; // 拍照界面

	public static boolean isRecording = false;// 是否正在录像
	public static boolean isTake_Pictureing = false;// 是否正在拍照

	public static final String CONTINUE_RECORD = "continueRecord";

	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

	// public static File file_Storage_Directory = null;// 文件存储目录
	// public static File videoFile_Storage_Directory = null;// 文件存储目录
	// public static File picFile_Storage_Directory = null;// 文件存储目录
	/** SD卡视频缩略图路径 */
	// public static String OPLAYER_VIDEO_THUMB =
	// file_Storage_Directory.getPath() + "/thumb/";

	public static final String TABLENAME_VIDEO = "tablename_video";
	public static final String TABLENAME_IMAGE = "tablename_image";

	public static ImageLoader imageLoader = ImageLoader.getInstance();
	public static DisplayImageOptions options = new DisplayImageOptions.Builder()//
			.showImageOnLoading(R.drawable.message_detail_img_backup)// image_listview_item_loading
			// .showImageOnLoading(R.drawable.ic_stub)//
			.showImageForEmptyUri(R.drawable.message_detail_img_backup)// ic_empty
			.showImageOnFail(R.drawable.message_detail_img_backup)// ic_error
			.cacheInMemory(true)//
			.cacheOnDisc(true)//
			.considerExifParams(true)//
			.displayer(new RoundedBitmapDisplayer(5))// 圆角大小
			.build();//
	//	OpenStack云  ：智汇市场  market.konkacloud.com             云上传 cloud.konkacloud.com
	//	115外网      ：智汇市场  market.konkacloud.cn              云上传 cloud.konkacloud.cn

	//	public static String domainName = "www.konkacloud.cn";// 域名 默认外网
	public static String domainName = "cloud.konkacloud.cn";// 域名 默认外网 2014 0714 cgp
	public static final String JD = "jd";// 进度
	public static final String UI = "ui";

	// public static String username = "";
	// public static String password = ""; //
	public static User user = null;

	public static VideoDBHelper videoDBHelper;
	public static ImageDBHelper imageDBHelper;
	public static boolean crash = false;// 默认 碰撞类型
	public static boolean foreverSave = false;// 默认 永久保存

	// http://www.konkacloud.cn（内网） ，
	// http://115.28.57.151:83 （外网）
	// public static final String inNet = "www.konkacloud.cn";
	public static final String inNet = "cloud.konkacloud.cn";
	public static final String outNet = "cloud.konkacloud.cn";

}
