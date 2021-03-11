package com.sipsd.flow.common.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.github.pagehelper.Page;

public class PageModel<T> implements Serializable {
	private static final long serialVersionUID = 4804053559968742915L;

	private long total;

	private List<T> data = new ArrayList<>();

	private List<T> rows = new ArrayList<>();

	public PageModel() {
	}

	public PageModel(List<T> list) {
		if (list instanceof Page) {
			Page<T> page = (Page<T>) list;
			this.data = (List<T>) page;
			this.total = page.getTotal();
		} else if (list instanceof java.util.Collection) {
			this.data = list;
			this.total = list.size();
		}
	}

	public PageModel(long total, List<T> rows) {
		this.total = total;
		this.rows = rows;
		this.data = rows;
	}

	public long getTotal() {
		return this.total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<T> getData() {
		return this.data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public List<T> getRows() {
		return this.rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}
}
