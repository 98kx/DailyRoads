package com.konka.dialyroads.frament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
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

public class SetImageFragment extends Fragment {
	ListView listView;
	SetImageAdapter adapter;
	List<Map<String, String>> lists = new ArrayList<Map<String, String>>();// 存放adapter的数据
	HashMap<String, String> map = new HashMap<String, String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ac_image_list, container, false);
		init(view);
		initimagedata();
		return view;
	}

	void init(View view) {
		listView = (ListView) view.findViewById(android.R.id.list);
		adapter = new SetImageAdapter();
		adapter.setLists(lists);
		listView.setAdapter(adapter);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(onItemClickListener);
	}

	void initimagedata() {
		lists.removeAll(lists);
		map.put("k", "照片像素");
		map.put("v", AppPara.getInstance().getImage_Resolution_Ratio().toString());
		lists.add(map);
		map = new HashMap<String, String>();
		map.put("k", "曝光度");
		map.put("v", AppPara.getInstance().getExposureCompensation() + "");

		lists.add(map);
		map = new HashMap<String, String>();
		map.put("k", "快门声音");
		map.put("v", AppPara.getInstance().isShutterSound() ? "开" : "关");
		lists.add(map);
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (position) {
			case 0:
				// 照片像素
				
				final List<Size> pictureSizes = CameraSupportedParameters.getInstance().getPictureSizes();
				System.out.println("pictureSizes="+pictureSizes);
				String[] data_pic_resolution = new String[pictureSizes.size()];
				for (int i = 0; i < pictureSizes.size(); i++) {
					data_pic_resolution[i] = pictureSizes.get(i).width + "x" + pictureSizes.get(i).height;
				}
				new AlertDialog.Builder(getActivity()).

				setTitle("设置照片分辨率").setItems(data_pic_resolution, new DialogInterface.OnClickListener() {//
							@Override
							public void onClick(DialogInterface dialog, int which) {// .
								AppPara.getInstance().getImage_Resolution_Ratio().setHeight(pictureSizes.get(which).height);
								AppPara.getInstance().getImage_Resolution_Ratio().setWidth(pictureSizes.get(which).width);

								initimagedata();
								adapter.notifyDataSetChanged();
							}
						}).create().show();
				break;

			case 1:
				// 曝光度
				String[] data_photosensibility = new String[] { "-12", "-11", "-10", "-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
				final int[] data_photosensibility_int = new int[] { -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
				new AlertDialog.Builder(getActivity()).setTitle("设置曝光度").setItems(data_photosensibility, new DialogInterface.OnClickListener() {//
							@Override
							public void onClick(DialogInterface dialog, int which) {//
								AppPara.getInstance().setExposureCompensation(data_photosensibility_int[which]);

								initimagedata();
								adapter.notifyDataSetChanged();
							}
						}).create().show();

				break;
			case 2:
				// 快门声音
				String[] data_shutter_sound = new String[] { "开", "关" };
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle("设置快门声音").setItems(data_shutter_sound, new DialogInterface.OnClickListener() {//
							@Override
							public void onClick(DialogInterface dialog, int which) {//
								AppPara.getInstance().setShutterSound(which == 0);
//								getmCamera.enableShutterSound(AppPara.getInstance().isShutterSound());// 控制声音
								initimagedata();
								adapter.notifyDataSetChanged();
							}
						});
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
				break;
			}

		}
	};

	class SetImageAdapter extends BaseAdapter {
		List<Map<String, String>> lists;

		public List<Map<String, String>> getLists() {
			return lists;
		}

		public void setLists(List<Map<String, String>> lists) {
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
			TextView textView = null;
			TextView text_content = null;
			convertView = LayoutInflater.from(getActivity()).inflate(R.layout.setdialoglistviewitem, null);
			textView = (TextView) convertView.findViewById(R.id.text);
			text_content = (TextView) convertView.findViewById(R.id.text_content);
			textView.setText(lists.get(position).get("k"));
			text_content.setText(lists.get(position).get("v"));
			return convertView;
		}
	}
}
