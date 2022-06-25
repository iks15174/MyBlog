package com.jiho.board.springbootaws.web;

import com.jiho.board.springbootaws.service.member.MemberService;
import com.jiho.board.springbootaws.service.member.dto.AuthMemberDto;
import com.jiho.board.springbootaws.web.dto.member.LoginRequestDto;
import com.jiho.board.springbootaws.web.dto.member.MemberResponseDto;
import com.jiho.board.springbootaws.web.dto.member.MemberSaveRequestDto;
import com.jiho.board.springbootaws.web.dto.member.TokenDto;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/auth/signup")
    public MemberResponseDto singup(@RequestBody MemberSaveRequestDto requestDto) {
        return memberService.signup(requestDto);
    }

    @PostMapping("/api/v1/auth/login")
    public TokenDto login(@RequestBody LoginRequestDto requestDto) throws Exception {
        return memberService.login(requestDto);

    }

    @GetMapping("/api/v1/auth/test")
    public void test() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(((AuthMemberDto) authentication.getPrincipal()).getUsername());
    }

}
