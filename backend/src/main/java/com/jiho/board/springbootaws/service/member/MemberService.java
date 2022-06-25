package com.jiho.board.springbootaws.service.member;

import javax.transaction.Transactional;

import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.exception.exceptions.CustomBasicException;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;
import com.jiho.board.springbootaws.service.member.dto.AuthMemberDto;
import com.jiho.board.springbootaws.util.JWTUtil;
import com.jiho.board.springbootaws.web.dto.member.LoginRequestDto;
import com.jiho.board.springbootaws.web.dto.member.MemberResponseDto;
import com.jiho.board.springbootaws.web.dto.member.MemberSaveRequestDto;
import com.jiho.board.springbootaws.web.dto.member.TokenDto;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @Transactional
    public MemberResponseDto signup(MemberSaveRequestDto requestDto) throws CustomBasicException {
        if (memberRepository.existsByEmailAndSocial(requestDto.getEmail(), requestDto.getSocial())) {
            throw new CustomBasicException(ErrorCode.EMAIL_DUPLICATED_ERROR);
        }

        Member member = requestDto.toEntity(passwordEncoder);
        return new MemberResponseDto(memberRepository.save(member));
    }

    @Transactional
    public TokenDto login(LoginRequestDto requestDto) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = requestDto.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        AuthMemberDto authMemberDto = (AuthMemberDto) authentication.getPrincipal();
        return jwtUtil.generateToken(authMemberDto);
    }
}
