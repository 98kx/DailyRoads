package com.konka.dialyroads.pojo;

public class ImageFileBean {
	private long photo_date;
	private String path;
	private String name;
	private String thumbpath;
	private String size;
	private String showName;
	private String resolution_ratio;
	private boolean onUploadSuccess;
	
	
	public boolean isOnUploadSuccess() {
		return onUploadSuccess;
	}
	public void setOnUploadSuccess(boolean onUploadSuccess) {
		this.onUploadSuccess = onUploadSuccess;
	}
	
	
	public String getResolution_ratio() {
		return resolution_ratio;
	}
	public void setResolution_ratio(String resolution_ratio) {
		this.resolution_ratio = resolution_ratio;
	}
	public String getShowName() {
		return showName;
	}
	public void setShowName(String showName) {
		this.showName = showName;
	}
	public String getThumbpath() {
		return thumbpath;
	}
	public void setThumbpath(String thumbpath) {
		this.thumbpath = thumbpath;
	}
	public long getPhoto_date() {
		return photo_date;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public void setPhoto_date(long photo_date) {
		this.photo_date = photo_date;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


}
