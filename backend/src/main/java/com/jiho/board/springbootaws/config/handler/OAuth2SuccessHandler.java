package com.jiho.board.springbootaws.config.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiho.board.springbootaws.service.member.dto.AuthMemberDto;
import com.jiho.board.springbootaws.util.JWTUtil;
import com.jiho.board.springbootaws.web.dto.member.TokenDto;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        AuthMemberDto authMemberDto = (AuthMemberDto) authentication.getPrincipal();
        try {
            TokenDto tokenDto = jwtUtil.generateToken(authMemberDto);
            response.setContentType("application/json");
            response.setStatus(200);
            response.setCharacterEncoding("UTF-8");
            response.getWriter()
                    .write(objectMapper.writeValueAsString(tokenDto));
        } catch (Exception e) {
            throw new IOException();
        }
    }

}
