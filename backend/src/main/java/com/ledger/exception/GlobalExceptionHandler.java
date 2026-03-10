package com.ledger.exception;

import com.ledger.controller.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Void> handleBusinessException(BusinessException ex) {
        log.error("Business exception: {} - {}", ex.getCode(), ex.getMessage());
        return Response.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Void> handleGeneralException(Exception ex) {
        log.error("General exception: {}", ex.getMessage(), ex);
        return Response.error("5000", "服务器错误");
    }
}
