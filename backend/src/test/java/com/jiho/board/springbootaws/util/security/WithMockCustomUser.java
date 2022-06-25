package com.jiho.board.springbootaws.util.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.jiho.board.springbootaws.domain.member.MemberRole;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String username() default "testUser@test.com";

    MemberRole role() default MemberRole.USER;

}
