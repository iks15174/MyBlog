package com.jiho.board.springbootaws.domain.member;

import lombok.Getter;

@Getter
public enum MemberRole {
    USER(ROLES.USER, "유저권한"),
    ADMIN(ROLES.ADMIN, "어드민권한");

    public static class ROLES {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }

    private String authority;
    private String description;

    private MemberRole(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }
}
