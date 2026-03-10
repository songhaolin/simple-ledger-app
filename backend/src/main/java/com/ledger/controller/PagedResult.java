package com.ledger.controller;

import lombok.Data;
import org.springframework.data.domain.Page;

/**
 * 分页结果
 */
@Data
public class PagedResult<T> {
    private java.util.List<T> content;
    private int currentPage;
    private long totalElements;
    private int totalPages;
    private int pageSize;

    /**
     * 从 Spring Data Page 创建
     */
    public static <T> PagedResult<T> from(Page<T> page) {
        PagedResult<T> result = new PagedResult<>();
        result.setContent(page.getContent());
        result.setCurrentPage(page.getNumber() + 1); // 页码从1开始
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setPageSize(page.getSize());
        return result;
    }
}
