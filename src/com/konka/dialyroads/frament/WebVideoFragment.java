package com.konka.dialyroads.frament;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.konka.dialyroads.R;
import com.konka.dialyroads.pojo.WebVideoFileInfo;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.MD5;
import com.konka.dialyroads.util.Net;
import com.konka.dialyroads.util.Util;

public class WebVideoFragment extends Fragment {
	private ListView listView;
	private ItemAdapter_video itemAdapter;
	private ProgressDialog pd; // 等待
	Context context;
	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.webvideofilelistview, container, false);

		context = getActivity();
		init(view);
		return view;
	}

	void init(View view) {
		listView = (ListView) view.findViewById(R.id.cloud_download_data);
		itemAdapter = new ItemAdapter_video();
		listView.setAdapter(itemAdapter);
		listView.setOnItemClickListener(onItemClickListener);
		init1();

	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			WebVideoFileInfo webPicFileInfo = itemAdapter.getLists().get(position);
//			Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
//			intent.putExtra("path", webPicFileInfo.getUrl());
//			intent.putExtra("title", webPicFileInfo.getTitle());
//			startActivity(intent);
			Util.playVideo(context, webPicFileInfo.getUrl());
		}
	};

	private void init1() {
		pd = ProgressDialog.show(getActivity(), "加载", "正在努力加载...");
		pd.setCanceledOnTouchOutside(true);
		new Thread() {
			@Override
			public void run() {
				super.run();
				List<WebVideoFileInfo> lists = Net.getvideoinfo(Assist.user.getUsername(), MD5.getMD5String(Assist.user.getPassword()));
				for (WebVideoFileInfo fileInfo : lists) {
					System.out.println(fileInfo.getUrl());
				}
				itemAdapter.setLists(lists);
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
			public TextView title;
			public ImageView image;
		}

		private List<WebVideoFileInfo> lists = new ArrayList<WebVideoFileInfo>();

		public List<WebVideoFileInfo> getLists() {
			return lists;
		}

		public void setLists(List<WebVideoFileInfo> lists) {
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
				convertView = getActivity().getLayoutInflater().inflate(R.layout.webfilelistview_item, parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.video_image);
				holder.title = (TextView) convertView.findViewById(R.id.video_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.setText(lists.get(position).getTitle());
			Assist.imageLoader.displayImage(lists.get(position).getThumbnail(), holder.image, Assist.options);
			// Assist.imageLoader.displayImage(lists.get(position).getLists().get(0),
			// holder.image, Assist.options, animateFirstListener);
			
			return convertView;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		view = null;
	}
}
