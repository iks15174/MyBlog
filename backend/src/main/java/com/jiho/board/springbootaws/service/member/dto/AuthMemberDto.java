package com.jiho.board.springbootaws.service.member.dto;

import java.util.Collection;
import java.util.Map;

import com.jiho.board.springbootaws.domain.member.Social;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AuthMemberDto extends User implements OAuth2User {
    private String name;
    private Social social;
    private Map<String, Object> attr;

    public AuthMemberDto(String email, String password, String name, Social social,
            Collection<? extends GrantedAuthority> authorities, Map<String, Object> attr) {
        this(email, password, name, social, authorities);
        this.attr = attr;
    }

    public AuthMemberDto(String email, String password, String name, Social social,
            Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        this.name = name;
        this.social = social;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attr;
    }
}
