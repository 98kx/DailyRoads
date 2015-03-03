package com.konka.dialyroads.frament;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.konka.dialyroads.R;
import com.konka.dialyroads.activity.LocalFileActivity;
import com.konka.dialyroads.dialog.MyDialog;
import com.konka.dialyroads.myinterface.CallBackResult;
import com.konka.dialyroads.pojo.VideoFileBean;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.FileUploadTask;
import com.konka.dialyroads.util.MyFileUtils;
import com.konka.dialyroads.util.Util;

public class LocalVideoFragment extends Fragment implements OnItemLongClickListener {
	private ListView listView;
	private ItemAdapter_video itemAdapter;
	private ProgressDialog pd; // 等待
	Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ac_image_list, container, false);
		init(view);
		context = getActivity();
		initdata();
		return view;
	}

	void init(View view) {
		listView = (ListView) view.findViewById(android.R.id.list);
		itemAdapter = new ItemAdapter_video();
		listView.setAdapter(itemAdapter);
		listView.setOnItemClickListener(onItemClickListener);
		listView.setOnItemLongClickListener(this);
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Util.playVideo(context, itemAdapter.getLists().get(position).getPath());
		}
	};

	private void initdata() {
		pd = ProgressDialog.show(getActivity(), "加载", "正在努力加载...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				List<VideoFileBean> videoFileBeans = Assist.videoDBHelper.getAllVideoFileBean();
				itemAdapter.setLists(videoFileBeans);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						itemAdapter.notifyDataSetChanged();
						pd.dismiss();
					}
				});
			}
		}.start();
	}

	class ItemAdapter_video extends BaseAdapter {
		private class ViewHolder {
			public TextView text;
			public ImageView image;
			public TextView resolution_ratio;
			public TextView textSize;
			public ImageView upload;
			public ImageView foreverSave;
		}

		private List<VideoFileBean> lists = new ArrayList<VideoFileBean>();

		public List<VideoFileBean> getLists() {
			return lists;
		}

		public void setLists(List<VideoFileBean> lists) {
			this.lists = lists;
		}

		@Override
		public int getCount() {
			return lists.size();
		}

		@Override
		public Object getItem(int position) {
			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.video_item, parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.textTime);
				holder.image = (ImageView) convertView.findViewById(R.id.imageType);
				holder.resolution_ratio = (TextView) convertView.findViewById(R.id.resolution_ratio);
				holder.textSize = (TextView) convertView.findViewById(R.id.textSize);
				holder.upload = (ImageView) convertView.findViewById(R.id.upload);
				holder.foreverSave = (ImageView) convertView.findViewById(R.id.imageStar);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			VideoFileBean videoFileBean = lists.get(position);
			long second = (videoFileBean.getEndtime() - videoFileBean.getStarttime()); // 秒
			String timestring = Util.getTimeString(second);
			holder.text.setText(videoFileBean.getShowName() + "(" + timestring + ")");
			holder.text.setTextColor(videoFileBean.isOnCrash() ? getResources().getColor(R.color.text_red) : getResources().getColor(R.color.text_lan));
			holder.textSize.setText(videoFileBean.getSize());
			holder.resolution_ratio.setText(videoFileBean.getResolution_ratio());
			holder.upload.setVisibility(videoFileBean.isOnUploadSuccess() ? View.VISIBLE : View.GONE);
			holder.foreverSave.setVisibility(videoFileBean.isForeverSave() ? View.VISIBLE : View.GONE);
			try {
				// 检测缩略图是否存在,不存在就自动创建
				MyFileUtils.checkVideoThumb(videoFileBean.getThumbpath(), videoFileBean.getPath());
				Assist.imageLoader.displayImage("file:///" + videoFileBean.getThumbpath(), holder.image, Assist.options);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}
	}

	private AlertDialog alertDialog = null;
	private VideoFileBean videoFileBean;

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		videoFileBean = itemAdapter.getLists().get(position);
		if (alertDialog == null) {
			String[] items = new String[] { "删除", "查看详情", "上传", "永久保存" };
			alertDialog = new AlertDialog.Builder(getActivity()).setTitle("提示").setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0: // 删除
						File file = new File(videoFileBean.getPath());
						if (!MyFileUtils.checkFileExists(file.getPath())) {
							if (Assist.videoDBHelper.del(videoFileBean))// 删除数据库
								if (itemAdapter.getLists().remove(videoFileBean))
									itemAdapter.notifyDataSetChanged();
						} else {
							if (FileUtils.deleteQuietly(file))// 删除文件，
								if (Assist.videoDBHelper.del(videoFileBean))// 删除数据库
									if (itemAdapter.getLists().remove(videoFileBean))
										itemAdapter.notifyDataSetChanged();
						}
						break;
					case 1: // 详情
						LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View view = inflater.inflate(R.layout.image_file_info, null);
						TextView size = (TextView) view.findViewById(R.id.size);// 文件大小
						TextView res = (TextView) view.findViewById(R.id.res);// 分辨率
						TextView format = (TextView) view.findViewById(R.id.format);// 文件格式

						size.setText(videoFileBean.getSize());
						res.setText(videoFileBean.getResolution_ratio());
						format.setText(videoFileBean.isOnCrash() ? "碰撞" : videoFileBean.isForeverSave() ? "永久保存" : "其他");

//						MyDialog.showCustomViewDialog(getActivity(), view);
						MyDialog.showViewDialog(context, view);
						break;
					case 2:// 上传
						if (Util.detectionNetwork(getActivity())) {// 检测网络
							if (Assist.user != null) {// 检测是否登录了
								final VideoFileBean tvideoFileBean = videoFileBean;
								FileUploadTask fileUploadTask = new FileUploadTask(getActivity(), tvideoFileBean.getPath(), Assist.user, FileUploadTask.TYPE_VIDEO);
								fileUploadTask.setCallBackResult(new CallBackResult() {
									@Override
									public void callbackResult(Boolean result) {
										if (result) {
											Assist.videoDBHelper.setUploadType(tvideoFileBean, true); // 更新数据库
											tvideoFileBean.setOnUploadSuccess(true); // 更新ui
											itemAdapter.notifyDataSetChanged();
										}
									}
								});
								fileUploadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);// 默认5线程
							} else {
								Util.sendBroadcast(context, Assist.TOAST, "请先登录");
								if (((LocalFileActivity) getActivity()).myDialog == null) {
									((LocalFileActivity) getActivity()).myDialog = new MyDialog(getActivity());
								}
								((LocalFileActivity) getActivity()).myDialog.showUserDialog();
							}
						}
						break;
					case 3:// 永久保存
						new Thread(new Runnable() {
							@Override
							public void run() {
								final VideoFileBean tvideoFileBean = videoFileBean;
								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Assist.videoDBHelper.setForeverSave(tvideoFileBean, true);
										tvideoFileBean.setForeverSave(true);
										itemAdapter.notifyDataSetChanged();
									}
								});
							}
						}).start();
						break;
					}
				}
			}).create();
		}
		if (!alertDialog.isShowing()) {
			alertDialog.show();
		}
		return false;
	}
}
