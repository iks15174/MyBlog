package com.jiho.board.springbootaws.service.member;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.domain.member.MemberRole;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;
import com.jiho.board.springbootaws.service.member.dto.AuthMemberDto;
import com.jiho.board.springbootaws.service.member.dto.OAuth2Attribute;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String clientName = userRequest.getClientRegistration().getClientName();
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(clientName, oAuth2User.getAttributes());
        Member member = saveSocialMember(oAuth2Attribute);
        AuthMemberDto authMemberDto = new AuthMemberDto(
                member.getEmail(),
                member.getPassword(),
                member.getName(),
                member.getSocial(),
                member.getRoleSet().stream().map(
                        role -> new SimpleGrantedAuthority(role.getAuthority())).collect(Collectors.toList()),
                oAuth2User.getAttributes());

        return authMemberDto;
    }

    private Member saveSocialMember(OAuth2Attribute oAuth2Attribute) {
        Optional<Member> result = memberRepository.findByEmail(oAuth2Attribute.getEmail());

        if (result.isPresent()) {
            if (result.get().getSocial() != oAuth2Attribute.getSocial()) {
                OAuth2Error oAuth2Error = new OAuth2Error(ErrorCode.EMAIL_DUPLICATED_ERROR.getMessage());
                throw new OAuth2AuthenticationException(oAuth2Error, oAuth2Error.toString());
            }
            return result.get();
        }

        Member member = Member.builder()
                .email(oAuth2Attribute.getEmail())
                .password(passwordEncoder.encode("1234"))
                .social(oAuth2Attribute.getSocial())
                .name(oAuth2Attribute.getName())
                .build();
        member.addMemberRole(MemberRole.USER);
        memberRepository.save(member);
        return member;
    }

}
