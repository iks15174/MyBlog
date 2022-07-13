package com.jiho.board.springbootaws.util.security;

import java.util.Arrays;

import com.jiho.board.springbootaws.domain.member.Social;
import com.jiho.board.springbootaws.service.member.dto.AuthMemberDto;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        AuthMemberDto principal = new AuthMemberDto(
                customUser.username(),
                customUser.password(),
                customUser.name(),
                customUser.social(),
                Arrays.asList(new SimpleGrantedAuthority(customUser.role().getAuthority())));

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }

}
