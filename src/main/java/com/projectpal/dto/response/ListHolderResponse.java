package com.projectpal.dto.response;

import java.util.List;

public final class ListHolderResponse<T> {

	public ListHolderResponse(List<T> list) {
		this.list = list;
		this.size = list.size();
	}

	private final List<T> list;

	private final int size;

	public List<T> getList() {
		return list;
	}

	public int getSize() {
		return size;
	}

}
