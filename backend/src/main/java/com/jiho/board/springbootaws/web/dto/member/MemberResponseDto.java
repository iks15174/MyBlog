package com.jiho.board.springbootaws.web.dto.member;

import com.jiho.board.springbootaws.domain.member.Member;

import lombok.Getter;

@Getter
public class MemberResponseDto {
    private String email;

    public MemberResponseDto(Member member) {
        this.email = member.getEmail();
    }
}
