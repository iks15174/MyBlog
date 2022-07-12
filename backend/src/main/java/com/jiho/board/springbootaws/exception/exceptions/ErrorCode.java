package com.jiho.board.springbootaws.exception.exceptions;

public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "A001", " Invalid Input Value"),
    INTERNAL_SERVER_ERROR(500, "A002", "Server Error"),
    INVALID_TYPE_VALUE(400, "A003", " Invalid Type Value"),
    HANDLE_ACCESS_DENIED(403, "A004", "Access is Denied"),

    // Member
    EMAIL_DUPLICATED_ERROR(400, "B001", " Duplicated user email"),
    UNEIXIST_USER(401, "B002", "Can't find user by Email and Social"),

    // Post
    UNEIXIST_POST(404, "C001", "Can't find post by id"),

    // Comment
    UNEIXIST_COMMENT(404, "D001", "Can't find comment by id"),

    // Tag
    TAG_DUPLICATED_ERROR(400, "E001", " Duplicated tag");


    private int status;
    private final String code;
    private final String message;

    private ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return this.code;
    }

    public int getStatus() {
        return this.status;
    }

}
