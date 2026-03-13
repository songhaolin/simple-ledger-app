package com.ledger.controller;

/**
 * 分页结果
 */
public class PagedResult<T> {
    private java.util.List<T> content;
    private int currentPage;
    private long totalElements;
    private int totalPages;
    private int pageSize;

    // Getters
    public java.util.List<T> getContent() {
        return content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPageSize() {
        return pageSize;
    }

    // Setters
    public void setContent(java.util.List<T> content) {
        this.content = content;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 从 Spring Data Page 创建
     */
    public static <T> PagedResult<T> fromPage(org.springframework.data.domain.Page<T> page) {
        PagedResult<T> result = new PagedResult<>();
        result.setContent(page.getContent());
        result.setCurrentPage(page.getNumber() + 1); // 页码从1开始
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setPageSize(page.getSize());
        return result;
    }
}
