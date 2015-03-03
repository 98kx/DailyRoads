package com.konka.dialyroads.util;

import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.protocol.HTTP;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.konka.dialyroads.myinterface.CallBackResult;
import com.konka.dialyroads.myinterface.CallBackSize;
import com.konka.dialyroads.pojo.User;

public class FileUploadTask extends AsyncTask<Object, Integer, Boolean> implements CallBackSize, CallBackResult {

	private ProgressDialog dialog = null;
	private Context context = null;
	private User user;
	private int fileAllSize;
	private String path;
	private int fileType;

	public static final int TYPE_VIDEO = 0; // 文件类型 视频文件
	public static final int TYPE_IMAGE = 1;// 图片文件

	/**
	 * 
	 * @param context
	 * @param VideoFileBean
	 * @param user
	 *            用户
	 * @param fileType
	 *            上传的文件类型，视频 0 图片1
	 */
	public FileUploadTask(Context context, String path, User user, int fileType) {
		this.context = context;
		this.path = path;
		this.user = user;
		this.fileType = fileType;
		fileAllSize = Util.getFilesize(path);
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setMessage("正在上传...");
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setProgress(0);
		dialog.show();
	}

	// 后台执行
	@Override
	protected Boolean doInBackground(Object... arg0) {
		MyMultipartEntity mpEntity = new MyMultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName(HTTP.UTF_8)); // 设置编码，防止中文乱码
		mpEntity.setCallBack(this);
		boolean ok = Net.uploadFile(path, fileType, user, mpEntity);// 上传提交

		return ok;
	}

	// 上传进度
	@Override
	protected void onProgressUpdate(Integer... progress) {
		dialog.setProgress(progress[0]);

	}

	// 完成后
	@Override
	protected void onPostExecute(Boolean result) {
		Util.sendBroadcast(context, Assist.TOAST, result ? "上传成功" : "上传失败");
		callBackResult.callbackResult(result);
		try {
			dialog.setProgress(100);
			dialog.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * size 已经上传的大小
	 */
	@Override
	public void callbackSize(int size) {
		// 调用publishProgress更新进度条
		publishProgress(size * 100 / fileAllSize);
	}

	@Override
	public void callbackResult(Boolean result) {

	}

	CallBackResult callBackResult;

	public void setCallBackResult(CallBackResult callBackResult) {
		this.callBackResult = callBackResult;
	}

}
