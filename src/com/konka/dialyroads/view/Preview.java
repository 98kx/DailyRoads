package com.konka.dialyroads.view;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.konka.dialyroads.R;
import com.konka.dialyroads.myinterface.MakeVideoInterface;
import com.konka.dialyroads.pojo.AppPara;
import com.konka.dialyroads.pojo.CameraSupportedParameters;
import com.konka.dialyroads.pojo.ImageFileBean;
import com.konka.dialyroads.pojo.VideoFileBean;
import com.konka.dialyroads.service.AccelerationService;
import com.konka.dialyroads.service.BackgroundWorkService;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.MD5;
import com.konka.dialyroads.util.MyFileUtils;
import com.konka.dialyroads.util.Util;
import com.konka.dialyroads.widget.MyApplication;

public class Preview extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener {
	private Camera mCamera = null;
	private SurfaceHolder mHolder = null;
	private MovieRecorder movieRecorder = new MovieRecorder();
	private ImageView iFocusRect;
	private Animation rotateAnimation;
	// private Context context;
	private Timer timer = new Timer();
	private Context context;

	public Preview(Context context) {
		super(context);
		init(context);
	}

	public Preview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public Preview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public Camera getmCamera() {
		return mCamera;
	}

	private SoundPool pool = null;
	private int id_success = 0;
	private int id_error = 0;
	private int id_pic = 0;

	// private int shutter_d200_open = 0;
	// private int shutter_d200_close = 0;

	// @SuppressWarnings("deprecation")
	void init(Context context) {
		this.context = context;
		mHolder = getHolder();
		// mHolder.setFixedSize(640, 480);
		// mHolder.setFixedSize(176, 144);
		mHolder.setKeepScreenOn(true);// 点亮屏幕不让黑掉
		mHolder.addCallback(this);
		// mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //
		// deprecated
		this.setOnTouchListener(this);
		rotateAnimation = AnimationUtils.loadAnimation(context, R.drawable.scale_anim);

		// 增加声音
		pool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
		id_success = pool.load(context, R.raw.focus_success, 1);// 成功
		id_error = pool.load(context, R.raw.focus_error, 1);// 失败
		id_pic = pool.load(context, R.raw.shutter_d200, 1);// 快门
		// 效果不怎么好
		// shutter_d200_open = pool.load(context, R.raw.shutter_d200_open, 1);//
		// 录像开
		// shutter_d200_close = pool.load(context, R.raw.shutter_d200_close,
		// 1);// 录像关

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// System.out.println("mCamera--timer" + mCamera + "--" +
				// timer);
				if (movieRecorder.videoFileBean != null) {
					if (movieRecorder.videoFileBean.getStarttime() != 0) {
						Intent intent = new Intent(Assist.TIMER);
						long time = System.currentTimeMillis() - movieRecorder.videoFileBean.getStarttime();// 已经录制的时长
						intent.putExtra(Assist.TIMER, Util.getTimeString(time));
						getContext().sendBroadcast(intent);
						if (time >= AppPara.getInstance().getLoopDuration() * 1000 * 60) {
							// 停止录像然后再开启
							takePicture();
							takePicture();
						}

					}
				}
			}
		}, 1000, 1000);
	}

	// -----------------------------------------------------------------------SurfaceHolder.Callback
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.openCamera(true); // 打开相机，并且预览
		// until the surfaceChanged call to start the
		// preview
		this.setWillNotDraw(false); // see
									// http://stackoverflow.com/questions/2687015/extended-surfaceviews-ondraw-method-never-called
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mHolder = holder;
		if (mHolder.getSurface() == null) {
			return;
		}
		if (mCamera == null) {
			return;
		}
		// Parameters p=mCamera.getParameters();
		// p.setPictureSize(width, height);
		// mCamera.setParameters(p);

		try {
			// Camera.Parameters parameters = mCamera.getParameters();
			// parameters.setPictureFormat(ImageFormat.JPEG);//
			mCamera.setPreviewDisplay(mHolder);
			// mCamera.setDisplayOrientation(Surface.ROTATION_0);
			// mCamera.setParameters(parameters);
			this.setPreviewSize();// -------------------------------------设置不对
			// startCameraPreview();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.closeCamera();
	}

	// -----------------------------------------------------------------------SurfaceHolder.Callback

	/**
	 * 拍照 录像 (前台录像用)
	 */

	public void takePicture() {

		// try {
		// List<Size> supportedPreviewSizes =
		// mCamera.getParameters().getSupportedVideoSizes();
		// for (Size s : supportedPreviewSizes) {
		// System.out.println(s.width + "x" + s.height);
		// }
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// List<Integer> list =
		// CameraSupportedParameters.getInstance().getPictureFormats();
		// System.out.println("list;"+list.size());
		// for (int i : list) {
		// System.out.println(i);
		// }
		CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
		System.out.println("profile.videoFrameRate:" + profile.videoFrameRate);

		System.out.println("高" + Assist.screenHeight);
		System.out.println("kuan" + Assist.screenWidth);
		// {
		// List<Size> PreviewSizes =
		// CameraSupportedParameters.getInstance().getPreviewSizes();
		// for (Size s : PreviewSizes) {
		// System.out.println(s.width + "x" + s.height);
		// }
		// }
		// System.out.println(mCamera.getParameters().flatten());

		if (!Assist.isTake_Pictureing) {// 拍照时候先聚焦，聚焦成功拍照，
			// getSupportedPreviewSizes(mCamera.getParameters());
			if (Assist.isTake_Picture && !Assist.isRecording) { // 拍照
				Assist.isTake_Pictureing = true;
				mCamera.enableShutterSound(AppPara.getInstance().isShutterSound());// 控制声音
				mCamera.autoFocus(myAutoFocusCallback);

			} else if (!Assist.isTake_Picture) { // 录像
				if (movieRecorder != null) {
					if (Assist.isRecording) {
						movieRecorder.stopRecording();
					} else {
						if (Util.getFreeMemory() > 100) {
							movieRecorder.startRecording();
						} else {
							return;
						}

					}
				}
			}
		}
	}

	/**
	 * 后台录像调用这个
	 */
	public void startRecording() {
		if (movieRecorder != null && !Assist.isRecording) {
			movieRecorder.startRecording();
		}
	}

	/**
	 * 后台录像调用这个
	 */
	public void stopRecording() {
		if (Assist.isRecording) {
			movieRecorder.stopRecording();
		}
	}

	public void onResume() {
		this.openCamera(true);
	}

	public void onPause() {
		if (Assist.isRecording) {
			stopRecording();
		}
		this.closeCamera();
		// System.gc();
	}

	int count = 0;

	public void openCamera(boolean start_preview) {
		try {
			if (mCamera != null) {
				mCamera.release();// 释放相机资源
				mCamera = null;
			}
			if (mCamera == null) {
				mCamera = Camera.open(0);
				// mCamera.getParameters().flatten();
				// System.out.println(mCamera.getParameters().flatten());
				// try {
				// List<Size> supportedPreviewSizes =
				// mCamera.getParameters().getSupportedVideoSizes();
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// for (Size s : supportedPreviewSizes) {
				// System.out.println(s.width + "x" + s.height);
				// }
				if (CameraSupportedParameters.getInstance() == null) {
					CameraSupportedParameters.init(mCamera);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mCamera = null;
			Util.sendBroadcast(getContext(), Assist.TOAST, "打开相机失败");
			err();
		}
		if (mCamera != null) {
			try {
				mCamera.setPreviewDisplay(mHolder);
				// mCamera.setPreviewCallback(mPreviewCallback);
				System.out.println("被调用了...........");
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setZoom(0);
				
				parameters.setFlashMode(AppPara.getInstance().getFlashmode());
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {// sdk17以上才有这个功能
					parameters.setRecordingHint(true);// 4.0这样有助于减少启动采集所需要的时间
					// parameters.setf
					/**
					 * Android 4.0.3引入可以使图像稳定化
					 */
					if (parameters.isVideoStabilizationSupported())
						parameters.setVideoStabilization(true);
					// if (Build.VERSION.SDK_INT >=
					// Build.VERSION_CODES.JELLY_BEAN_MR1) // sdk17以上才有这个功能
					mCamera.enableShutterSound(AppPara.getInstance().isShutterSound());// 控制声音
					// mCamera.enableShutterSound(false);// 控制声音

				}
				// ---------
				List<String> scene_modes = parameters.getSupportedSceneModes();
				String scene_mode = setupValuesPref(scene_modes, "preference_scene_mode", Camera.Parameters.SCENE_MODE_AUTO);
				if (scene_mode != null) {
					parameters.setSceneMode(scene_mode);
				}

				// parameters.setZoom(5);

				// parameters.setPictureFormat(CameraSupportedParameters.getInstance().getPreviewFormats().get(0));//
				// 每秒显示多少帧

				parameters.setPictureFormat(ImageFormat.JPEG);
				// parameters.set("jpeg-quality", 85);
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);//
				// parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//
				// parameters.setPictureSize(AppPara.getInstance().getImage_Resolution_Ratio().getWidth()//
				// ,
				// AppPara.getInstance().getImage_Resolution_Ratio().getHeight());
				// parameters.setPreviewSize(AppPara.getInstance().getImage_Resolution_Ratio().getWidth()//
				// ,
				// AppPara.getInstance().getImage_Resolution_Ratio().getHeight());
				parameters.setExposureCompensation(AppPara.getInstance().getExposureCompensation());
				// 曝光度
				// parameters.setZoom(parameters.getMaxZoom());
				mCamera.setParameters(parameters);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// setPreviewSize();// /---

			if (start_preview) {
				startCameraPreview();
				try {
					mCamera.autoFocus(Assist.isBackgroundWork ? null : myAutoFocusCallback);// 后台录像不监听
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void startCameraPreview() {
		try {
			// this.setPreviewSize();
			mCamera.startPreview();
			System.out.println("mCamera.getParameters().getPictureSize().height" + mCamera.getParameters().getPreviewSize().height);
			System.out.println("mCamera.getParameters().getPictureSize().w" + mCamera.getParameters().getPreviewSize().width);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置预览大小
	 */
	private void setPreviewSize() {//
		if (mCamera == null) {
			return;
		}
		// set optimal preview size
		Camera.Parameters parameters = mCamera.getParameters();
		List<Camera.Size> preview_sizes = parameters.getSupportedPreviewSizes();
		if (preview_sizes.size() > 0) {
			Camera.Size best_size = preview_sizes.get(0);
			for (Camera.Size size : preview_sizes) {
				if (size.width * size.height > best_size.width * best_size.height) {
					best_size = size;
				}
			}
			System.out.println("best_size.width" + best_size.width);
			System.out.println("best_size.height" + best_size.height);
			// parameters.setPreviewSize(720, 480);
			parameters.setPreviewSize(best_size.width, best_size.height);
			mCamera.setParameters(parameters);
		}

	}

	private void closeCamera() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);

			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			System.gc();
		}
	}

	/**
	 * 聚焦回调接口
	 */
	private AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (iFocusRect == null)
				iFocusRect = (ImageView) ((Activity) getContext()).findViewById(R.id.iFocusRect);
			if (iFocusRect != null)
				iFocusRect.setImageResource(success ? R.drawable.focus_focused : R.drawable.focus_focus_failed);
			if (Assist.isTake_Pictureing) {
				mCamera.takePicture(new ShutterCallback() {
					@Override
					public void onShutter() {
						if (AppPara.getInstance().isShutterSound()) {
//							pool.play(id_pic, 1.0f, 1.0f, 0, 0, 1.0f);
						}
					}
				}, null, new PhotoHandler());

			}
		}

	};

	/**
	 * 录像
	 * 
	 * @author Administrator
	 * 
	 */
	class MovieRecorder implements MakeVideoInterface {
		private MediaRecorder mediarecorder = null;
		// public boolean recording = false;
		private VideoFileBean videoFileBean;

		private boolean onCrash;
		private boolean foreverSave;

		public MovieRecorder() {
			super();
		}

		/**
		 * 初始化数据参数
		 */
		void init() {
		}

		private void delTempVideo() {

			List<VideoFileBean> videoFileBeans = Assist.videoDBHelper.getAllVideoFileBean();
			for (VideoFileBean videoFileBean : videoFileBeans) {
				if (!videoFileBean.isForeverSave()) {
					if (FileUtils.deleteQuietly(new File(videoFileBean.getPath()))) {
						Assist.videoDBHelper.del(videoFileBean);
					}
				}
			}
		}

		@Override
		public void startRecording() {
			// 检测文件临时文件是否超出，并处理
			if (Util.getFreeMemory() < 100 || Assist.videoDBHelper.getTempVideoSize() >= AppPara.getInstance().getTempFolderSize()) {
				delTempVideo();
				if (Util.getFreeMemory() < 100) {
					Util.sendBroadcast(getContext(), Assist.TOAST, "内存卡容量不足");
					return;
				}
			}
			if (mediarecorder == null) {
				mediarecorder = new MediaRecorder();// 创建mediarecorder对象
			}
			videoFileBean = new VideoFileBean();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {// sdk17以上才有这个功能

			}
			if (mCamera != null)
				mCamera.unlock();
			mediarecorder.setCamera(mCamera);
			if (AppPara.getInstance().isRECsound()) {
				mediarecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);// 声音
			}

			mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			// mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 声音
			mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
			int width = AppPara.getInstance().getVideo_Resolution_Ratio().getWidth();
			int height = AppPara.getInstance().getVideo_Resolution_Ratio().getHeight();
			// mediarecorder.setVideoSize(1280, 720);
			// mediarecorder.setVideoSize(144, 176);
			// mediarecorder.setVideoSize(640, 480);
			// mediarecorder.setProfile(profile)
			mediarecorder.setVideoSize(width, height);
			System.out.println("width:" + width + "----height;" + height);
			// mediarecorder.setVideoFrameRate(15);
			CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
			// CamcorderProfile profile =
			// CamcorderProfile.get(CamcorderProfile.QUALITY_720P);

			System.out.println("profile.videoCodec" + profile.videoCodec);
			// CamcorderProfile profile =
			// CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
			mediarecorder.setVideoFrameRate(profile.videoFrameRate);
			// System.out.println("profile.videoBitRate:"+profile.videoBitRate);
			// System.out.println("profile.videoFrameHeight:"+profile.videoFrameHeight);
			// System.out.println("profile.videoFrameWidth:"+profile.videoFrameWidth);

			// mediarecorder.setVideoEncodingBitRate(30000);
			// mediarecorder.setVideoEncodingBitRate(8000000);
			mediarecorder.setVideoEncodingBitRate(profile.videoBitRate);
			mediarecorder.setPreviewDisplay(Preview.this.getHolder().getSurface());

			long time = System.currentTimeMillis();
			String filename = Util.getFileName(time);
			videoFileBean.setName(filename);
			videoFileBean.setResolution_ratio(width + "x" + height);
			videoFileBean.setShowName(Util.getShowName(time));
			videoFileBean.setStarttime(time);
			videoFileBean.setPath(MyApplication.VIDEOFILE_STORAGE_DIRECTORY + "/" + filename + ".mp4");
			// mediarecorder.setOrientationHint(90);
			// videoFileBean.setPath(Assist.videoFile_Storage_Directory.getPath()
			// + "/" + filename + ".mp4");

			mediarecorder.setOutputFile(videoFileBean.getPath());
			// 准备录制
			try {
				mediarecorder.setOnInfoListener(null);
				mediarecorder.setOnErrorListener(null);

				mediarecorder.prepare();
				mediarecorder.start();
				Assist.isRecording = true;

			} catch (Exception e) {
				e.printStackTrace();
			}
			// if(AppPara.getInstance().isRECsound()){
			// pool.play(shutter_d200_open, 1.0f, 1.0f, 0, 0, 1.0f);
			// }
			// 开始录制 发生正在录制的广播 用来播放动画
			Util.sendBroadcast(getContext(), Assist.START_RECORD);

		}

		// 正真停止录像，
		@Override
		public void stopRecording() {
			// Util.sendBroadcast(getContext(), Assist.);
			if (mediarecorder != null) {
				mediarecorder.stop();
				mediarecorder.release();
				// mediarecorder.reset();
				mediarecorder = null;// stopRecording
				videoFileBean.setEndtime(System.currentTimeMillis());

				String thumbpath = MyApplication.THUMBFILE_STORAGE_DIRECTORY + MD5.getMD5String(videoFileBean.getName());
				MyFileUtils.createVideoThumbFile(thumbpath, videoFileBean.getPath());

				videoFileBean.setThumbpath(thumbpath);
				videoFileBean.setSize(Util.getFileSizesTostring(videoFileBean.getPath()));
				onCrash = Assist.crash;
				Assist.crash = false;
				foreverSave = Assist.foreverSave;
				Assist.foreverSave = false;
				// videoFileBean.setOnCrash(true);
				videoFileBean.setOnCrash(onCrash);
				videoFileBean.setForeverSave(foreverSave);
				long i = Assist.videoDBHelper.save(videoFileBean);
				System.out.println(i == 1L ? "存入数据库成功" : "存入数据库失败");
				videoFileBean = null;
				Assist.isRecording = false;
				System.gc();
			}
			// if(AppPara.getInstance().isRECsound()){
			// pool.play(shutter_d200_close, 1.0f, 1.0f, 0, 0, 1.0f);
			// }
			Util.sendBroadcast(getContext(), Assist.STOP_RECORD);
		}

		// 停止录像，可能还需要继续录像
		@Override
		public void release() {
			Util.sendBroadcast(getContext(), Assist.STOP_RECORD);
			if (mediarecorder != null) {
				mediarecorder.stop();
				mediarecorder.release();
				videoFileBean.setEndtime(System.currentTimeMillis());
				videoFileBean = null;
				System.gc();
			}
		}

	}

	// 拍照回调接口
	class PhotoHandler implements PictureCallback {
		private ImageFileBean imageFileBean;

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			imageFileBean = new ImageFileBean();
			long time = System.currentTimeMillis();
			String filename = Util.getFileName(time);

			imageFileBean.setName(filename);
			imageFileBean.setShowName(Util.getShowName(time));
			imageFileBean.setPhoto_date(time);
			imageFileBean.setPath(MyApplication.IMAGEFILE_STORAGE_DIRECTORY + "/" + filename + ".jpg");
			imageFileBean.setResolution_ratio(AppPara.getInstance().getImage_Resolution_Ratio().toString());
			try {
				File picfile = new File(imageFileBean.getPath());
				if (!picfile.exists()) {
					picfile.createNewFile();
				}
				FileUtils.writeByteArrayToFile(picfile, data);
				// FileOutputStream outStream = new
				// FileOutputStream(imageFileBean.getPath());
				// outStream.write(data);
				// outStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			{
				String thumbpath = MyApplication.THUMBFILE_STORAGE_DIRECTORY + MD5.getMD5String(imageFileBean.getName());
				imageFileBean.setThumbpath(thumbpath);
				MyFileUtils.createImageThumbFile(thumbpath, imageFileBean.getPath());
			}
			imageFileBean.setSize(Util.getFileSizesTostring(imageFileBean.getPath()));

			// long i =
			Assist.imageDBHelper.save(imageFileBean);
			Util.sendBroadcast(getContext(), Assist.STOP_TAKEPICTURE);

			camera.setPreviewCallback(null);
			camera.stopSmoothZoom();
			// camera.getParameters().setZoom(0);
			camera.stopPreview();
			startCameraPreview();
			Assist.isTake_Pictureing = false;
		}
	}

	void startScaleAnim() {
		if (iFocusRect == null)
			iFocusRect = (ImageView) ((Activity) getContext()).findViewById(R.id.iFocusRect);
		iFocusRect.startAnimation(rotateAnimation);
	}

	/**
	 * onTouch事件
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!Assist.isTake_Pictureing) { // 正在拍照时候不能点击屏幕聚焦
				mCamera.autoFocus(myAutoFocusCallback);
				startScaleAnim();
			}
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	protected void onDetachedFromWindow() {
		if (timer != null) {
			this.timer.cancel();
			this.timer = null;
		}
		this.closeCamera();
		this.movieRecorder = null;
		System.gc();
		super.onDetachedFromWindow();
	}

	private String setupValuesPref(List<String> values, String key, String default_value) {
		if (values != null && values.size() > 0) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
			String value = sharedPreferences.getString(key, default_value);
			if (!values.contains(value)) {
				if (values.contains(default_value))
					value = default_value;
				else
					value = values.get(0);
			}

			// now save, so it's available for PreferenceActivity
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(key, value);
			editor.apply();

			return value;
		} else {
			return null;
		}
	}

	// private PreviewCallback mPreviewCallback = new PreviewCallback() {
	//
	// @Override
	// public void onPreviewFrame(byte[] data, Camera camera) {
	//
	// }
	// };
	AlertDialog alertDialog;

	private void err() {
		if (alertDialog == null) {
			alertDialog = new AlertDialog.Builder(context).setTitle("提示").setMessage("打开相机失败,建议重启手机后再试")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
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
			alertDialog.setCanceledOnTouchOutside(false);
			alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_BACK:
						return true;
					}
					return false;
				}
			});
		}
		alertDialog.show();
	}
}
