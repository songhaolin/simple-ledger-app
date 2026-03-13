package com.ledger.controller;

import com.ledger.exception.BusinessException;
import com.ledger.exception.JwtValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器（使用ApiResponse避免与Spring框架冲突）
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理JWT验证异常
     */
    @ExceptionHandler(JwtValidationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleJwtValidationException(JwtValidationException ex) {
        log.warn("JWT validation failed: {}", ex.getMessage());
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode("4011");
        response.setMessage(ex.getMessage());
        return response;
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        String code = ex.getCode();
        String message = ex.getMessage();

        HttpStatus status = HttpStatus.BAD_REQUEST;

        // 特殊处理：4011错误码或1004错误码对应401状态
        if (BusinessException.ErrorCodes.UNAUTHORIZED.equals(code) ||
            BusinessException.ErrorCodes.WRONG_PASSWORD.equals(code)) {
            status = HttpStatus.UNAUTHORIZED;
            log.warn("Unauthorized access: {}", message);
        } else {
            log.error("Business exception: {} - {}", code, message);
        }

        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);

        return ResponseEntity.status(status).body(response);
    }

    /**
     * 处理IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument exception: {}", ex.getMessage());
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode("4001");
        response.setMessage(ex.getMessage());
        return response;
    }

    /**
     * 处理MissingRequestHeaderException
     */
    @ExceptionHandler(org.springframework.web.bind.MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleMissingRequestHeaderException(org.springframework.web.bind.MissingRequestHeaderException ex) {
        log.warn("Missing request header: {}", ex.getHeaderName());
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode("4011");
        response.setMessage("缺少Authorization头");
        return response;
    }

    /**
     * 处理验证异常
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        log.error("Validation exception: {}", ex.getMessage());
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode("4001");
        response.setMessage(ex.getBindingResult().getFieldError() != null ?
            ex.getBindingResult().getFieldError().getDefaultMessage() : "参数验证失败");
        return response;
    }

    /**
     * 处理TypeMismatchException (参数类型转换失败)
     */
    @ExceptionHandler(org.springframework.beans.TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleTypeMismatchException(org.springframework.beans.TypeMismatchException ex) {
        log.error("Type mismatch exception: {}", ex.getMessage());
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode("4001");
        response.setMessage("参数类型错误");
        return response;
    }

    /**
     * 处理ServletRequestBindingException (参数绑定失败)
     */
    @ExceptionHandler(org.springframework.web.bind.ServletRequestBindingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleServletRequestBindingException(org.springframework.web.bind.ServletRequestBindingException ex) {
        log.error("Servlet request binding exception: {}", ex.getMessage());
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode("4001");
        response.setMessage("参数绑定失败");
        return response;
    }

    /**
     * 处理HttpMessageNotReadableException
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleHttpMessageNotReadableException(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        log.error("Http message not readable: {}", ex.getMessage());
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode("4001");
        response.setMessage("参数格式错误");
        return response;
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneralException(Exception ex) {
        log.error("General exception: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode("5000");
        response.setMessage("服务器错误");
        return response;
    }
}
