package com.konka.dialyroads.myinterface;

import java.util.List;

import com.konka.dialyroads.pojo.ImageFileBean;

public interface ImagesDAOInterface {
	long save(ImageFileBean picFileBean);

	List<ImageFileBean> getAll_Image();

	/**
	 * 
	 * @return 最近一次拍照的imagefilebean
	 */
	ImageFileBean getLatest();
	
	boolean del(ImageFileBean picFileBean);

	void saveUploadtype(ImageFileBean picFileBean, Boolean onUploadSuccess);
}