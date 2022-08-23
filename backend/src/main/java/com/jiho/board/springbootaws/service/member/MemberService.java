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
import com.jiho.board.springbootaws.web.dto.util.JwtValidateResultDto;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public Member getCurLoginedUser() {
        AuthMemberDto memberDto = (AuthMemberDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member curLoginedUser = memberRepository.findByEmail(memberDto.getUsername()).orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_USER));
        return curLoginedUser; 
    }

    @Transactional
    public void checkCurUserIsAuthor(Member author) {
        if(!author.getEmail().equals(((AuthMemberDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())){
            throw new CustomBasicException(ErrorCode.FORBIDDEN_USER);
        }
    }

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
        TokenDto newToken = jwtUtil.generateToken(authMemberDto);
        Member member = memberRepository.findByEmail(authMemberDto.getUsername())
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_USER));
        member.updateRefreshToken(newToken.getRefreshToken());
        return newToken;
    }

    @Transactional
    public TokenDto reissue(TokenDto requestDto) {
        JwtValidateResultDto jwtValidateResultDto = jwtUtil.validate(requestDto.getRefreshToken());
        if (jwtValidateResultDto.getResultCode() == JWTUtil.JwtCode.DENIED) {
            throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE);
        } else if (jwtValidateResultDto.getResultCode() == JWTUtil.JwtCode.EXPIRED) {
            Authentication authentication = jwtUtil.extract(jwtValidateResultDto.getClaims());
            AuthMemberDto authMemberDto = (AuthMemberDto) authentication.getPrincipal();
            Member member = memberRepository.findByEmail(authMemberDto.getUsername())
                    .orElseThrow(() -> new CustomBasicException(ErrorCode.FORBIDDEN_USER));
            member.clearRefreshToken();
            throw new CustomBasicException(ErrorCode.FORBIDDEN_USER);
        } else {
            Authentication authentication = jwtUtil.extract(jwtValidateResultDto.getClaims());
            AuthMemberDto authMemberDto = (AuthMemberDto) authentication.getPrincipal();
            Member member = memberRepository.findByEmail(authMemberDto.getUsername())
                    .orElseThrow(() -> new CustomBasicException(ErrorCode.FORBIDDEN_USER));
            if (!member.getRefreshToken().equals(requestDto.getRefreshToken())
                    || requestDto.getRefreshToken().isEmpty()) {
                member.clearRefreshToken();
                throw new CustomBasicException(ErrorCode.FORBIDDEN_USER);
            }
            TokenDto generatedToken = jwtUtil.generateToken(authMemberDto);
            member.updateRefreshToken(generatedToken.getRefreshToken());
            return generatedToken;
        }
    }
}
