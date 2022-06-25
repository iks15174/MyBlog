package com.jiho.board.springbootaws.web.dto.member;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {

    private String accessToken;
}
