package com.jiho.board.springbootaws.web;

import com.jiho.board.springbootaws.service.member.MemberService;
import com.jiho.board.springbootaws.service.member.dto.AuthMemberDto;
import com.jiho.board.springbootaws.web.dto.member.LoginRequestDto;
import com.jiho.board.springbootaws.web.dto.member.MemberResponseDto;
import com.jiho.board.springbootaws.web.dto.member.MemberSaveRequestDto;
import com.jiho.board.springbootaws.web.dto.member.TokenDto;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<MemberResponseDto> singup(@RequestBody MemberSaveRequestDto requestDto) {
        return ResponseEntity.ok().body(memberService.signup(requestDto));
    }

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDto requestDto) throws Exception {
        return ResponseEntity.ok().body(memberService.login(requestDto));

    }

    @PostMapping("/api/v1/auth/reissue")
    public ResponseEntity<TokenDto> postMethodName(@RequestBody TokenDto requestDto) {
        return ResponseEntity.ok().body(memberService.reissue(requestDto));
    }
}
