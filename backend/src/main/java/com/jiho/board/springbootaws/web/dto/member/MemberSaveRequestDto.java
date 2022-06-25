package com.jiho.board.springbootaws.web.dto.member;

import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRole;
import com.jiho.board.springbootaws.domain.member.Social;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class MemberSaveRequestDto {

    private String email;
    private String password;
    private String name;
    private Social social;

    public Member toEntity(PasswordEncoder passwordEncoder) {
        Member memberEntity = Member.builder().email(this.email).name(this.name)
                .password(passwordEncoder.encode(this.password)).social(this.social)
                .build();

        memberEntity.addMemberRole(MemberRole.USER);
        return memberEntity;
    }

}
