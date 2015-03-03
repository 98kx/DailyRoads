package com.konka.dialyroads.frament;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.konka.dialyroads.activity.ShowOneImageFileActivity;
import com.konka.dialyroads.dialog.MyDialog;
import com.konka.dialyroads.myinterface.CallBackResult;
import com.konka.dialyroads.pojo.ImageFileBean;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.FileUploadTask;
import com.konka.dialyroads.util.MyFileUtils;
import com.konka.dialyroads.util.Util;

public class LocalImageFragment extends Fragment implements OnItemLongClickListener {
	private ListView listView;
	private ItemAdapter_image itemAdapter;
	private ProgressDialog pd; // 等待
	Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ac_image_list, container, false);
		init(view);
		context = getActivity();
		init1();
		return view;
	}

	void init(View view) {
		listView = (ListView) view.findViewById(android.R.id.list);
		itemAdapter = new ItemAdapter_image();
		listView.setAdapter(itemAdapter);
		listView.setOnItemClickListener(onItemClickListener);
		listView.setOnItemLongClickListener(this);
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(context, ShowOneImageFileActivity.class);
			intent.putExtra("url", itemAdapter.getLists().get(position).getPath());
			startActivity(intent);
		}
	};

	private void init1() {
		pd = ProgressDialog.show(getActivity(), "加载", "正在努力加载...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				// List<WebPicFileInfo> lists = Net.getpicdata(Assist.username,
				// MD5.getMD5String(Assist.password));
				List<ImageFileBean> listsBeans = Assist.imageDBHelper.getAll_Image();

				itemAdapter.setLists(listsBeans);
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

	class ItemAdapter_image extends BaseAdapter {
		private class ViewHolder {
			public TextView text;
			public ImageView image;
			public TextView textSize;
			public TextView resolution_ratio;
			public ImageView upload;
		}

		// private ImageLoadingListener animateFirstListener = new
		// AnimateFirstDisplayListener();
		private List<ImageFileBean> lists = new ArrayList<ImageFileBean>();

		public List<ImageFileBean> getLists() {
			return lists;
		}

		public void setLists(List<ImageFileBean> lists) {
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
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.video_item, parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.textTime);
				holder.image = (ImageView) convertView.findViewById(R.id.imageType);
				holder.textSize = (TextView) convertView.findViewById(R.id.textSize);
				holder.resolution_ratio = (TextView) convertView.findViewById(R.id.resolution_ratio);
				holder.upload = (ImageView) convertView.findViewById(R.id.upload);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.text.setText(lists.get(position).getShowName());
			holder.textSize.setText(lists.get(position).getSize());
			holder.resolution_ratio.setText(lists.get(position).getResolution_ratio());
			holder.upload.setVisibility(lists.get(position).isOnUploadSuccess() ? View.VISIBLE : View.GONE);

			// 检测缩略图是否存在,不存在就自动创建
			MyFileUtils.checkImageThumb(lists.get(position).getThumbpath(), lists.get(position).getPath());

			Assist.imageLoader.displayImage("file:///" + lists.get(position).getThumbpath(), holder.image, Assist.options);

			return convertView;
		}
	}

	private AlertDialog alertDialog = null;
	private ImageFileBean imageFileBean;

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		imageFileBean = itemAdapter.getLists().get(position);
		if (alertDialog == null) {
			String[] items = new String[] { "删除", "查看详情", "上传" };
			alertDialog = new AlertDialog.Builder(getActivity()).setTitle("提示").setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0: // 删除
						File file = new File(imageFileBean.getPath());

						if (!MyFileUtils.checkFileExists(file.getPath())) {
							if (Assist.imageDBHelper.del(imageFileBean)) {// 删除数据库
								if (itemAdapter.getLists().remove(imageFileBean))
									itemAdapter.notifyDataSetChanged();
							}
							Util.sendBroadcast(context, Assist.TOAST, "删除成功");
						} else {
							boolean ok = FileUtils.deleteQuietly(file) && Assist.imageDBHelper.del(imageFileBean) && itemAdapter.getLists().remove(imageFileBean);
							if (ok) {
								itemAdapter.notifyDataSetChanged();
							}
							Util.sendBroadcast(context, Assist.TOAST, ok ? "删除成功" : "删除失败");
						}

						break;
					case 1: // 详情
						LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View view = inflater.inflate(R.layout.image_file_info, null);
						TextView size = (TextView) view.findViewById(R.id.size);// 文件大小
						TextView res = (TextView) view.findViewById(R.id.res);// 分辨率
						TextView format = (TextView) view.findViewById(R.id.format);// 文件格式

						size.setText(imageFileBean.getSize());
						res.setText(imageFileBean.getResolution_ratio());
						format.setText("jpeg");

//						MyDialog.showCustomViewDialog(getActivity(), view);
						MyDialog.showViewDialog(context, view);
						break;
					case 2:// 上传
						if (Util.detectionNetwork(getActivity())) {// 检测网络
							if (Assist.user != null) {// 检测是否登录了
								final ImageFileBean timageFileBean = imageFileBean;
								FileUploadTask fileUploadTask = new FileUploadTask(getActivity(), timageFileBean.getPath(), Assist.user, FileUploadTask.TYPE_IMAGE);
								fileUploadTask.setCallBackResult(new CallBackResult() {
									@Override
									public void callbackResult(Boolean result) {
										if (result) {
											Assist.imageDBHelper.saveUploadtype(timageFileBean, true); // 更新数据库
											timageFileBean.setOnUploadSuccess(true); // 更新ui
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
