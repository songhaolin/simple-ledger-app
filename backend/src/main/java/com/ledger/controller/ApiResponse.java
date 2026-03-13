package com.ledger.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * API响应格式（泛型版本）
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    // Setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 创建成功响应
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    /**
     * 创建失败响应
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String code, String message, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        return ResponseEntity.status(status).body(response);
    }

    /**
     * 快速创建成功响应（200）
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return success(data);
    }

    /**
     * 快速创建失败响应（400）
     */
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String code, String message) {
        return error(code, message, HttpStatus.BAD_REQUEST);
    }

    /**
     * 快速创建未授权响应（401）
     */
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return error("4011", message, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 快速创建服务器错误响应（500）
     */
    public static <T> ResponseEntity<ApiResponse<T>> serverError(String message) {
        return error("5000", message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
