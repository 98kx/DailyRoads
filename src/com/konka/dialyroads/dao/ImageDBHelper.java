package com.konka.dialyroads.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.konka.dialyroads.myinterface.ImagesDAOInterface;
import com.konka.dialyroads.pojo.ImageFileBean;
import com.konka.dialyroads.util.Assist;

public class ImageDBHelper implements ImagesDAOInterface {
	private BaseDBHelper databaseHelper;

	public ImageDBHelper(Context context) {
		databaseHelper = new BaseDBHelper(context);
	}

	/**
	 * @param videoFileBean
	 * @return 1表示成功 0 表示失败
	 */
	@Override
	public long save(ImageFileBean inmageFileBean) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		try {
			db.execSQL("insert into " + Assist.TABLENAME_IMAGE + " (" + //
					"photo_date, " + // 拍照时间11
					"path ," + // 路径22
					"name," + // 名称33
					"thumbpath," + // 名称44
					"size," + // 55
					"showName," + // 66
					"resolution_ratio," + // 77
					"onuploadsuccess" + // 77
					") " + //
					" values(?,?,?,?,?,?,?,?)", //
					new Object[] { inmageFileBean.getPhoto_date(),//
							inmageFileBean.getPath(),//
							inmageFileBean.getName(), //
							inmageFileBean.getThumbpath(),//
							inmageFileBean.getSize(), //
							inmageFileBean.getShowName(),//
							inmageFileBean.getResolution_ratio(), //
							inmageFileBean.isOnUploadSuccess() //
					});//
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

		return 0;
	}

	@Override
	public List<ImageFileBean> getAll_Image() {
		List<ImageFileBean> image_Files = new ArrayList<ImageFileBean>();
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		long photo_date;
		String path;
		String name;
		String thumbpath;
		String size;
		String showName;
		String resolution_ratio;
		boolean onuploadsuccess;

		Cursor cursor = db.rawQuery("select * from " + Assist.TABLENAME_IMAGE + " order by _id desc ", null);
		while (cursor.moveToNext()) {
			photo_date = cursor.getLong(cursor.getColumnIndex("photo_date"));
			path = cursor.getString(cursor.getColumnIndex("path"));
			name = cursor.getString(cursor.getColumnIndex("name"));
			thumbpath = cursor.getString(cursor.getColumnIndex("thumbpath"));
			size = cursor.getString(cursor.getColumnIndex("size"));
			showName = cursor.getString(cursor.getColumnIndex("showName"));
			resolution_ratio = cursor.getString(cursor.getColumnIndex("resolution_ratio"));
			onuploadsuccess = cursor.getInt(cursor.getColumnIndex("onuploadsuccess")) > 0;
//			cursor.get
			ImageFileBean imageFileBean = new ImageFileBean();

			imageFileBean.setPhoto_date(photo_date);
			imageFileBean.setPath(path);
			imageFileBean.setName(name);
			imageFileBean.setThumbpath(thumbpath);
			imageFileBean.setSize(size);
			imageFileBean.setShowName(showName);
			imageFileBean.setResolution_ratio(resolution_ratio);
			imageFileBean.setOnUploadSuccess(onuploadsuccess);

			image_Files.add(imageFileBean);
		}
		return image_Files;
	}

	@Override
	public ImageFileBean getLatest() {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		long photo_date;
		String path;
		String name;
		String thumbpath;
		String size;
		String showName;
		String resolution_ratio;

		Cursor cursor = db.rawQuery("select * from " + Assist.TABLENAME_IMAGE + " order by _id desc ", null);
		if (cursor.moveToNext()) {
			photo_date = cursor.getLong(cursor.getColumnIndex("photo_date"));
			path = cursor.getString(cursor.getColumnIndex("path"));
			name = cursor.getString(cursor.getColumnIndex("name"));
			thumbpath = cursor.getString(cursor.getColumnIndex("thumbpath"));
			size = cursor.getString(cursor.getColumnIndex("size"));
			showName = cursor.getString(cursor.getColumnIndex("showName"));
			resolution_ratio = cursor.getString(cursor.getColumnIndex("resolution_ratio"));

			ImageFileBean imageFileBean = new ImageFileBean();

			imageFileBean.setPhoto_date(photo_date);
			imageFileBean.setPath(path);
			imageFileBean.setName(name);
			imageFileBean.setThumbpath(thumbpath);
			imageFileBean.setSize(size);
			imageFileBean.setShowName(showName);
			imageFileBean.setResolution_ratio(resolution_ratio);

			return imageFileBean;
		}
		return null;
	}

	@Override
	public boolean del(ImageFileBean picFileBean) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		int i = db.delete(Assist.TABLENAME_IMAGE, "path= '" + picFileBean.getPath() + "'", null);
		return i > 0;
	}

	/**
	 * 上传成功后修改数据库
	 * 
	 * @param id
	 * @param type
	 */
	@Override
	public void saveUploadtype(ImageFileBean picFileBean, Boolean onUploadSuccess) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("onuploadsuccess", onUploadSuccess);
		db.update(Assist.TABLENAME_IMAGE, contentValues, "path= '" + picFileBean.getPath() + "'", null);
		db.close();
		System.out.println("修改数据库ok");
	}
}
