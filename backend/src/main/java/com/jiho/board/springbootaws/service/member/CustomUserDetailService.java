package com.jiho.board.springbootaws.service.member;

import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.service.member.dto.AuthMemberDto;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

        private final MemberRepository memberRepository;

        @Override
        @Transactional
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Member member = memberRepository.findByEmail(username)
                                .orElseThrow(() -> new UsernameNotFoundException(username + "-> 해당 유저가 존재하지 않습니다."));

                return new AuthMemberDto(
                                member.getEmail(),
                                member.getPassword(),
                                member.getName(),
                                member.getSocial(),
                                member.getRoleSet().stream()
                                                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                                                .collect(Collectors.toSet()));

        }
}
