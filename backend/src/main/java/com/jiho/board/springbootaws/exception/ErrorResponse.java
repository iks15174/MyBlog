package com.jiho.board.springbootaws.exception;

import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponse {

    private String message;
    private int status;
    private String code;

    public ErrorResponse(ErrorCode code) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.code = code.getCode();
    }

    public ErrorResponse(RuntimeException e, int status) {
        this.message = e.getMessage();
        this.status = status;
    }

}
