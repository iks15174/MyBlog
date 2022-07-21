package com.jiho.board.springbootaws.config.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiho.board.springbootaws.exception.ErrorResponse;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;
import com.jiho.board.springbootaws.util.JWTUtil;
import com.jiho.board.springbootaws.web.dto.util.JwtValidateResultDto;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = resolveToken(request);
        if (StringUtils.hasText(jwt)) {
            JwtValidateResultDto resultDto = jwtUtil.validate(jwt);
            ErrorResponse errorResponse = null;
            switch (resultDto.getResultCode()) {
                case DENIED:
                    errorResponse = new ErrorResponse(ErrorCode.FORBIDDEN_USER);
                    response.setContentType("application/json");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter()
                            .write(objectMapper.writeValueAsString(errorResponse));
                    return;
                case EXPIRED:
                    errorResponse = new ErrorResponse(ErrorCode.EXPIRED_USER);
                    response.setContentType("application/json");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter()
                            .write(objectMapper.writeValueAsString(errorResponse));
                    return;
                case ACCESS:
                    Authentication authentication = jwtUtil.extract(resultDto.getClaims());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    break;
                default:
                    break;

            }
        }

        filterChain.doFilter(request, response);

    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) &&
                bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }

        return null;
    }

}
