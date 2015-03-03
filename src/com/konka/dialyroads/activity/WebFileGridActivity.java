package com.konka.dialyroads.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.konka.dialyroads.R;
import com.konka.dialyroads.pojo.WebPicFileInfo;
import com.konka.dialyroads.util.Assist;

public class WebFileGridActivity extends Activity {
	private GridView listView;
	private ImageAdapter imageAdapter;
	private Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置横屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.ac_image_grid);
		WebPicFileInfo webPicFileInfo = (WebPicFileInfo) getIntent().getSerializableExtra("webPicFileInfo");
		listView = (GridView) findViewById(R.id.gridview);
		imageAdapter = new ImageAdapter();
		imageAdapter.setWebPicFileInfo(webPicFileInfo);
		listView.setAdapter(imageAdapter);
		listView.setOnItemClickListener(listener);
	}

	public class ImageAdapter extends BaseAdapter {
		private WebPicFileInfo webPicFileInfo;

		@Override
		public int getCount() {

			List<String> lists = webPicFileInfo.getLists();
			if (lists != null) {
				return webPicFileInfo.getLists().size();
			}
			return 0;
		}

		public WebPicFileInfo getWebPicFileInfo() {
			return webPicFileInfo;
		}

		public void setWebPicFileInfo(WebPicFileInfo webPicFileInfo) {
			this.webPicFileInfo = webPicFileInfo;
		}

		@Override
		public Object getItem(int position) {
			return webPicFileInfo.getLists().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ImageView imageView;
			if (convertView == null) {
				imageView = (ImageView) getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
			} else {
				imageView = (ImageView) convertView;
			}

			Assist.imageLoader.displayImage(webPicFileInfo.getLists().get(position), imageView, Assist.options);

			return imageView;
		}
	}

	OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(context, ShowOneImageFileActivity.class);
			String url = imageAdapter.getWebPicFileInfo().getLists().get(position);
			intent.putExtra("url", url);
			startActivity(intent);
		}
	};
}
