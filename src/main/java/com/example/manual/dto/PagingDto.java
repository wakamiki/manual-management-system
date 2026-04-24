package com.example.manual.dto;

import org.springframework.data.domain.Page;

public class PagingDto {

    private int currentPage = 0;// 0始まり（0が1ページ目）
    private int pageSize = 10;
    private int totalPages = 0;
    private Long totalElements;
    private boolean hasNext;
    private boolean hasPrevious;



    // getter

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    // setter
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    //メソッド
    public static PagingDto from(Page<?> page) {
        PagingDto pagingDto = new PagingDto();
        pagingDto.setCurrentPage(page.getNumber());
        pagingDto.setTotalPages(page.getTotalPages());
        pagingDto.setTotalElements(page.getTotalElements());
        pagingDto.setPageSize(page.getSize());
        pagingDto.setHasPrevious(page.hasPrevious());
        pagingDto.setHasNext(page.hasNext());
        return pagingDto;
    }

}
