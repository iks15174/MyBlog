package com.jiho.board.springbootaws.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.domain.member.Social;
import com.jiho.board.springbootaws.web.dto.member.LoginRequestDto;
import com.jiho.board.springbootaws.web.dto.member.MemberSaveRequestDto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mvc;
    private String urlPrefix;

    @AfterEach
    public void tearDown() throws Exception {
        memberRepository.deleteAll();
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        urlPrefix = "http://localhost:" + port;
    }

    @Test
    public void Members_회원가입한다() throws Exception {
        String email = "jiho@test.com";
        String password = "1111";
        String name = "ParkJiHo";
        Social social = Social.NoSocial;

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .email(email)
                .password(password)
                .name(name)
                .social(social)
                .build();

        String url = urlPrefix + "/api/v1/auth/signup";

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        List<Member> allMember = memberRepository.findAll();
        assertThat(allMember.get(0).getEmail()).isEqualTo(email);
        assertThat(passwordEncoder.matches(
                password, allMember.get(0).getPassword()))
                .isTrue();
        assertThat(allMember.get(0).getName()).isEqualTo(name);
        assertThat(allMember.get(0).getSocial()).isEqualTo(social);

    }

    @Test
    public void 동일한_이메일로_회원가입_할_수_없다() throws Exception {
        String email = "jiho@test.com";
        String password = "1111";
        String name = "ParkJiHo";
        Social social = Social.NoSocial;
        memberRepository.save(Member.builder().email(email).name(name).social(social)
                .password(passwordEncoder.encode(password)).build());

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .email(email)
                .password(password)
                .name(name)
                .social(social)
                .build();

        String url = urlPrefix + "/api/v1/auth/signup";

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 로그인할_수_있다() throws Exception {
        String email = "jiho@test.com";
        String password = "1111";
        String name = "ParkJiHo";
        Social social = Social.NoSocial;
        memberRepository.save(Member.builder().email(email).name(name).social(social)
                .password(passwordEncoder.encode(password)).build());

        LoginRequestDto requestDto = LoginRequestDto.builder().email(email).password(password).build();

        String url = urlPrefix + "/api/v1/auth/login";

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isString());
        
    }
}
