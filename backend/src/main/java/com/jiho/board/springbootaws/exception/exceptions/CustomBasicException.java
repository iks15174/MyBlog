package com.jiho.board.springbootaws.exception.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomBasicException extends RuntimeException {

    private ErrorCode errorCode;

    public CustomBasicException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CustomBasicException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpStatus changeToHttpStatus() {
        switch (this.errorCode.getStatus()) {
            case 200:
                return HttpStatus.OK;
            case 201:
                return HttpStatus.CREATED;
            case 202:
                return HttpStatus.ACCEPTED;
            case 204:
                return HttpStatus.NO_CONTENT;
            case 400:
                return HttpStatus.BAD_REQUEST;
            case 401:
                return HttpStatus.UNAUTHORIZED;
            case 403:
                return HttpStatus.FORBIDDEN;
            case 404:
                return HttpStatus.NOT_FOUND;
            case 405:
                return HttpStatus.METHOD_NOT_ALLOWED;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
