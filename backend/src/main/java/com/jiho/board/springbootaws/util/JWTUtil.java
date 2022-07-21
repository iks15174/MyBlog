package com.jiho.board.springbootaws.util;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import com.jiho.board.springbootaws.domain.member.Social;
import com.jiho.board.springbootaws.service.member.dto.AuthMemberDto;
import com.jiho.board.springbootaws.web.dto.member.TokenDto;
import com.jiho.board.springbootaws.web.dto.util.JwtValidateResultDto;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJws;

@Component
public class JWTUtil {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String NAME_KEY = "name";
    private static final String SOCIAL_KEY = "social";
    private String secretKey = "randomSecretNeedChange";
    private long accessExpire = 60; // 1 hour
    private long refreshExpire = 60 * 24 * 3; // 3days

    public TokenDto generateToken(AuthMemberDto authMemberDto) throws Exception {
        String authorities = authMemberDto.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(accessExpire).toInstant()))
                .setSubject(authMemberDto.getUsername())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(NAME_KEY, authMemberDto.getName())
                .claim(SOCIAL_KEY, authMemberDto.getSocial())
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes("UTF-8"))
                .compact();

        String refreshToken = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(refreshExpire).toInstant()))
                .setSubject(authMemberDto.getUsername())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(NAME_KEY, authMemberDto.getName())
                .claim(SOCIAL_KEY, authMemberDto.getSocial())
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes("UTF-8"))
                .compact();
                
        return TokenDto.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public JwtValidateResultDto validate(String tokenStr) {
        DefaultClaims claims = null;
        try {
            DefaultJws defaultJws = (DefaultJws) Jwts.parser()
                    .setSigningKey(secretKey.getBytes("UTF-8"))
                    .parseClaimsJws(tokenStr);
            claims = (DefaultClaims) defaultJws.getBody();
            if (claims.get(AUTHORITIES_KEY) == null) {
                return JwtValidateResultDto.builder().resultCode(JwtCode.DENIED).claims(null).build();
            }
        } catch (ExpiredJwtException e) {
            return JwtValidateResultDto.builder().resultCode(JwtCode.EXPIRED).claims(null).build();
        } catch (Exception e) {
            return JwtValidateResultDto.builder().resultCode(JwtCode.DENIED).claims(null).build();
        }
        return JwtValidateResultDto.builder().resultCode(JwtCode.ACCESS).claims(claims).build();

    }

    public Authentication extract(DefaultClaims claims) {
        Authentication authentication = null;

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                claims.get(AUTHORITIES_KEY).toString().split(",")).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        UserDetails principal = new AuthMemberDto(
                claims.getSubject(),
                "",
                (String) claims.get(NAME_KEY),
                (Social) Social.valueOf((String) claims.get(SOCIAL_KEY)),
                authorities);
        authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        return authentication;
    }

    public static enum JwtCode {
        DENIED,
        ACCESS,
        EXPIRED;
    }
}
