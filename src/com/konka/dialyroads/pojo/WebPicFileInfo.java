package com.konka.dialyroads.pojo;

import java.io.Serializable;
import java.util.List;

public class WebPicFileInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public WebPicFileInfo(String create_time, List<String> lists, int id) {
		super();
		this.create_time = create_time;
		this.lists = lists;
		this.id = id;
	}
	private String create_time;
	public String getCreate_time() {
		return create_time;
	}
	public WebPicFileInfo() {
		super();
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public List<String> getLists() {
		return lists;
	}
	public void setLists(List<String> lists) {
		this.lists = lists;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	private List<String> lists;
	private int id;
}
