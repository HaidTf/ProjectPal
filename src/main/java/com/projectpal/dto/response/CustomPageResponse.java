package com.projectpal.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

public final class CustomPageResponse<T> {

	public CustomPageResponse(Page<T> page) {
		this.content = page.getContent();
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.currentPage = page.getNumber();
		this.pageSize = page.getSize();
		this.isFirst = page.isFirst();
		this.isLast = page.isLast();
	}

	private final List<T> content;
	
	private final long totalElements;
	
	private final int totalPages;
	
	private final int currentPage;
	
	private final int pageSize;
	
	private final boolean isFirst;
	
	private final boolean isLast;

	public List<T> getContent() {
		return content;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public boolean isLast() {
		return isLast;
	}

}
