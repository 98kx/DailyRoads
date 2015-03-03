package com.konka.dialyroads.frament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.konka.dialyroads.R;
import com.konka.dialyroads.pojo.AppPara;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.Net;

public class SetBaseFragment extends Fragment {
	ListView listView;
	SetCommonAdapter adapter;
	List<Map<String, String>> lists = new ArrayList<Map<String, String>>();// 存放adapter的数据
	HashMap<String, String> map = new HashMap<String, String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ac_image_list, container, false);
		initdata();
		init(view);
		return view;
	}

	void init(View view) {
		listView = (ListView) view.findViewById(android.R.id.list);
		adapter = new SetCommonAdapter();
		adapter.setLists(lists);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(onItemClickListener);
	}

	void initdata() {
		lists.removeAll(lists);
		map.put("k", "事故电话");
		map.put("v", AppPara.getInstance().getTelephone());
		lists.add(map);
		map = new HashMap<String, String>();
		map.put("k", "临时文件夹大小");
		map.put("v", AppPara.getInstance().getTempFolderSize() / 1024 >= 1 ? AppPara.getInstance().getTempFolderSize() / 1024 + "G" : AppPara
				.getInstance().getTempFolderSize() + "M");
		lists.add(map);
		map = new HashMap<String, String>();
		map.put("k", "网络设置");
		map.put("v", wlhj[i]);
		lists.add(map);
	}

	String[] wlhj = { "外网", "内网" };
	static int i = 1;
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (position) {
			case 0:
				set_phone_number();
				break;
			case 1:
				// 临时文件夹大小
				String[] data_temp_folder_size = new String[] { "300M", "500M", "1G", "2G", "4G", "8G" };
				final int[] data_temp_folder_size_v = { 300, 500, 1024, 1024 * 2, 1024 * 4, 1024 * 8 };
				AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setTitle("临时文件夹大小")
						.setItems(data_temp_folder_size, new DialogInterface.OnClickListener() {//
									@Override
									public void onClick(DialogInterface dialog, int which) {//
										AppPara.getInstance().setTempFolderSize(data_temp_folder_size_v[which]);
										initdata();
										adapter.notifyDataSetChanged();
									}
								}).create();
				alertDialog.show();
				break;
			case 2:
				setNet();
				break;
			}

		}
	};

	class SetCommonAdapter extends BaseAdapter {
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

	EditText editText;

	/**
	 * 紧急联系号码
	 */
	private void set_phone_number() {
		Builder builder = new Builder(getActivity());
		builder.setTitle("设置紧急联系号码");

		editText = new EditText(getActivity());
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		editText.setBackgroundResource(R.drawable.shape);
		editText.setTextColor(getActivity().getResources().getColor(R.color.textcolor));

		LinearLayout layout = new LinearLayout(getActivity());

		layout.setGravity(Gravity.CENTER_VERTICAL);

		LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		editText.setLayoutParams(p);

		layout.setPadding(10, 15, 10, 0);

		layout.setLayoutParams(p);

		layout.addView(editText);

		// 取出参数的值
		editText.setText(AppPara.getInstance().getTelephone());
		builder.setView(layout);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String e = editText.getText().toString();

				if (e.length() != 0) {
					AppPara.getInstance().setTelephone(e);
					SetBaseFragment.this.initdata();
					adapter.notifyDataSetChanged();
				}
			}
		});
		builder.setNegativeButton("取消", null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void setNet() {
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setTitle("选择网络环境").setItems(wlhj, new DialogInterface.OnClickListener() {//
					@Override
					public void onClick(DialogInterface dialog, int which) {//
						// AppPara.getInstance().setTempFolderSize(data_temp_folder_size_v[which]);
						i = which;
						if (i == 0) {// 外网
							Assist.domainName = Assist.outNet;
						} else {
							Assist.domainName = Assist.inNet;
						}
						initdata();
						Net.init();
						Assist.user = null;
						adapter.notifyDataSetChanged();
					}
				}).create();
		alertDialog.show();
	}
}
