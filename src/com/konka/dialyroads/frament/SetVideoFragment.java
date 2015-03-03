package com.konka.dialyroads.frament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.konka.dialyroads.R;
import com.konka.dialyroads.pojo.AppPara;
import com.konka.dialyroads.pojo.CameraSupportedParameters;

public class SetVideoFragment extends Fragment {
	ListView listView;
	SetVideoAdapter adapter;
	List<Map<String, String>> lists = new ArrayList<Map<String, String>>();// 存放adapter的数据
	HashMap<String, String> map = new HashMap<String, String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ac_image_list, container, false);
		init(view);
		initvideodata();
		return view;
	}

	void init(View view) {
		listView = (ListView) view.findViewById(android.R.id.list);
		adapter = new SetVideoAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(onItemClickListener);
	}

	void initvideodata() {
		lists.removeAll(lists);
		map.put("k", "循环录影时长");
		map.put("v", AppPara.getInstance().getLoopDuration() + "分");
		lists.add(map);
		map = new HashMap<String, String>();
		map.put("k", "视频分辨率");
		map.put("v", AppPara.getInstance().getVideo_Resolution_Ratio().toString());
		lists.add(map);
		map = new HashMap<String, String>();
		map.put("k", "声音录制");
		map.put("v", AppPara.getInstance().isRECsound() ? "开" : "关");
		lists.add(map);
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (position) {
			case 0:
				// 循环录像时长
				String[] data = { "1分钟", "5分钟", "10分钟", "15分钟", "20分钟", "25分钟", "30分钟" };
				final int[] data_v = { 1, 5, 10, 15, 20, 25, 30 };

				new AlertDialog.Builder(getActivity())

				.setTitle("设置循环录影时长")

				.setItems(data, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						AppPara.getInstance().setLoopDuration(data_v[which]);

						initvideodata();
						adapter.notifyDataSetChanged();
					}
				}).create().show();
				break;
			case 1:
				// 视频分辨率
				final List<Size> videoSizes = CameraSupportedParameters.getInstance().getVideoSizes();
				String[] data_fbl = new String[videoSizes.size()];
				for (int i = 0; i < videoSizes.size(); i++) {
					data_fbl[i] = videoSizes.get(i).width + "x" + videoSizes.get(i).height;
				}
				Builder builder = new AlertDialog.Builder(getActivity()).setTitle("设置视频分辨率").setItems(data_fbl, new DialogInterface.OnClickListener() {//
							@Override
							public void onClick(DialogInterface dialog, int which) {//
								AppPara.getInstance().getVideo_Resolution_Ratio().setWidth(videoSizes.get(which).width);
								AppPara.getInstance().getVideo_Resolution_Ratio().setHeight(videoSizes.get(which).height);

								initvideodata();
								adapter.notifyDataSetChanged();
							}
						});
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
				break;
			case 2:
				// 声音录制
				String[] data_video_sound = new String[] { "开", "关" };
				builder = new AlertDialog.Builder(getActivity()).

				setTitle("声音录制").setItems(data_video_sound, new DialogInterface.OnClickListener() {//
							@Override
							public void onClick(DialogInterface dialog, int which) {//
								AppPara.getInstance().setRECsound(which == 0);

								initvideodata();
								adapter.notifyDataSetChanged();
							}
						});
				alertDialog = builder.create();
				alertDialog.show();
				break;

			}
		}
	};

	class SetVideoAdapter extends BaseAdapter {

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
			convertView = LayoutInflater.from(getActivity()).inflate(R.layout.setdialoglistviewitem, null);
			TextView textView = (TextView) convertView.findViewById(R.id.text);
			TextView text_content = (TextView) convertView.findViewById(R.id.text_content);

			textView.setText(lists.get(position).get("k"));
			text_content.setText(lists.get(position).get("v"));
			return convertView;
		}

	}
}
