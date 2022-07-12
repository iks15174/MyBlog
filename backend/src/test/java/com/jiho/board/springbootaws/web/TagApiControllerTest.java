package com.jiho.board.springbootaws.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.domain.member.Social;
import com.jiho.board.springbootaws.domain.tag.Tag;
import com.jiho.board.springbootaws.domain.tag.TagRepository;
import com.jiho.board.springbootaws.util.security.WithMockCustomUser;
import com.jiho.board.springbootaws.web.dto.member.MemberSaveRequestDto;
import com.jiho.board.springbootaws.web.dto.tag.TagSaveRequestDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TagApiControllerTest {

    private Member testMember;

    private MockMvc mvc;

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    public void clean() {
        tagRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        testMember = memberRepository.save(MemberSaveRequestDto.builder().email("testUser@test.com").password("1234")
                .name("testUser").social(Social.NoSocial).build().toEntity(passwordEncoder));
    }

    @Test
    @WithMockCustomUser
    public void Tag_저장한다() throws Exception {
        String name = "test_tag";
        TagSaveRequestDto requestDto = TagSaveRequestDto.builder().name(name).build();
        String url = "http://localhost:" + port + "/api/v1/tags";

        mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto))).andExpect(status().isOk());

        List<Tag> tag = tagRepository.findAll();
        assertThat(tag.size()).isEqualTo(1);
        assertThat(tag.get(0).getName()).isEqualTo(name);
    }

    @Test
    @WithMockCustomUser
    void 중복된_TAG_저장하면_예외를_발생시킨다() throws Exception {
        String name = "test_tag";
        TagSaveRequestDto requestDto = TagSaveRequestDto.builder().name(name).build();
        String url = "http://localhost:" + port + "/api/v1/tags";

        mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto))).andExpect(status().isOk());

        List<Tag> tag = tagRepository.findAll();
        assertThat(tag.size()).isEqualTo(1);
        assertThat(tag.get(0).getName()).isEqualTo(name);

        mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto))).andExpect(status().isBadRequest());
    }

    @Test
    public void Tag_가져온다() throws Exception {
        List<Tag> tags = createTags(1, 10);
        String url = "http://localhost:" + port + "/api/v1/tags";
        ResultActions actions = mvc.perform(get(url)).andExpect(status().isOk());
        for (int i = 0; i < tags.size(); i++){
            actions = actions.andExpect(MockMvcResultMatchers.jsonPath("$.[" + i + "].name").value(tags.get(i).getName()));
        }
    }

    @Test
    public void Tag_검색한다() throws Exception {
        List<Tag> tags = createTags(1, 10);
        List<Tag> tagsContainOne = tags.stream().filter(t -> t.getName().contains("1")).collect(Collectors.toList());
        String url = "http://localhost:" + port + "/api/v1/tags" + "?name=1";
        ResultActions actions = mvc.perform(get(url)).andExpect(status().isOk());
        for (int i = 0; i < tagsContainOne.size(); i++){
            actions = actions.andExpect(MockMvcResultMatchers.jsonPath("$.[" + i + "].name").value(tagsContainOne.get(i).getName()));
        }
    }

    private List<Tag> createTags(int start, int end) {
        String baseTagName = "tag";
        List<Tag> tags = new ArrayList<>();
        IntStream.rangeClosed(start, end).forEach(i -> {
            tags.add(Tag.builder().name(baseTagName + i).build());
        });
        return tagRepository.saveAll(tags);
    }
}
