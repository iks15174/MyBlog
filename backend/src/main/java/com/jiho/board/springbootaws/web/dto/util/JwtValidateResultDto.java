package com.jiho.board.springbootaws.web.dto.util;

import com.jiho.board.springbootaws.util.JWTUtil.JwtCode;

import io.jsonwebtoken.impl.DefaultClaims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JwtValidateResultDto {
    private JwtCode resultCode;
    private DefaultClaims claims;
}
