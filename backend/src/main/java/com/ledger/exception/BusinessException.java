package com.ledger.exception;

import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;
    private final String message;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    // 错误码常量
    public static final class ErrorCodes {
        public static final String INVALID_PHONE = "1001";
        public static final String INVALID_PASSWORD = "1002";
        public static final String PHONE_EXISTS = "1003";
        public static final String WRONG_PASSWORD = "1004";
        public static final String INVALID_PARAM = "4001";
    }
}
