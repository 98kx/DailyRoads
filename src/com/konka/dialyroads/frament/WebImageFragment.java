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
import com.konka.dialyroads.activity.WebFileGridActivity;
import com.konka.dialyroads.pojo.WebPicFileInfo;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.MD5;
import com.konka.dialyroads.util.Net;

public class WebImageFragment extends Fragment {
	private ListView listView;
	private ItemAdapter_image itemAdapter;
	private ProgressDialog pd; // 等待
	Context context;
	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ac_image_list, container, false);
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

	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(context, WebFileGridActivity.class);
			WebPicFileInfo webPicFileInfo = itemAdapter.getLists().get(position);
			Bundle bundle = new Bundle();
			bundle.putSerializable("webPicFileInfo", webPicFileInfo);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};

	private void init1() {
		pd = ProgressDialog.show(getActivity(), "加载", "正在努力加载...");
		pd.setCanceledOnTouchOutside(true);
		new Thread() {
			@Override
			public void run() {
				super.run();
				List<WebPicFileInfo> lists = Net.getpicdata(Assist.user.getUsername(), MD5.getMD5String(Assist.user.getPassword()));
				if (lists.size() > 0) {
					for (String fileInfo : lists.get(0).getLists()) {
						System.out.println(fileInfo);
					}
					;
					itemAdapter.setLists(lists);
				}
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
		}

		// private ImageLoadingListener animateFirstListener = new
		// AnimateFirstDisplayListener();
		private List<WebPicFileInfo> lists = new ArrayList<WebPicFileInfo>();

		public List<WebPicFileInfo> getLists() {
			return lists;
		}

		public void setLists(List<WebPicFileInfo> lists) {
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
				convertView = getActivity().getLayoutInflater().inflate(R.layout.item_list_image, parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.image = (ImageView) convertView.findViewById(R.id.image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.text.setText(lists.get(position).getCreate_time());

			Assist.imageLoader.displayImage(lists.get(position).getLists().get(0), holder.image, Assist.options);
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
