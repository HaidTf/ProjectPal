package com.projectpal.dto.response;

import java.util.ArrayList;

public class ListHolderResponse<T> {

	public ListHolderResponse(ArrayList<T> list) {
		this.list = list;
		this.size = list.size();
	}

	private final ArrayList<T> list;

	private final int size;

	public ArrayList<T> getList() {
		return list;
	}

	public int getSize() {
		return size;
	}

}
