package com.ledger.controller;

import lombok.Data;

/**
 * 统一响应格式
 */
@Data
public class Response<T> {

    private boolean success;
    private T data;

    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static <T> Response<T> error(String code, String message) {
        Response<T> response = new Response<>();
        response.setSuccess(false);
        return response;
    }
}
