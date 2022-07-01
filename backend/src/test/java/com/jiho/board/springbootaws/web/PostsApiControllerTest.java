package com.jiho.board.springbootaws.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.domain.member.Social;
import com.jiho.board.springbootaws.domain.postTag.PostTag;
import com.jiho.board.springbootaws.domain.postTag.PostTagRepository;
import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.domain.posts.PostsRepository;
import com.jiho.board.springbootaws.domain.tag.Tag;
import com.jiho.board.springbootaws.domain.tag.TagRepository;
import com.jiho.board.springbootaws.util.security.WithMockCustomUser;
import com.jiho.board.springbootaws.web.dto.common.TagDto;
import com.jiho.board.springbootaws.web.dto.member.MemberSaveRequestDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsSaveRequestDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsUpdateRequestDto;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

        private Member testMember;

        private MockMvc mvc;

        @LocalServerPort
        private int port;

        @Autowired
        private WebApplicationContext context;

        @Autowired
        private PostsRepository postsRepository;

        @Autowired
        private MemberRepository memberRepository;

        @Autowired
        private PostTagRepository postTagRepository;

        @Autowired
        private TagRepository tagRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @AfterEach
        public void tearDown() throws Exception {
                postsRepository.deleteAll();
                tagRepository.deleteAll();
                memberRepository.deleteAll();
        }

        @BeforeEach
        public void setup() {
                mvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                testMember = memberRepository.save(MemberSaveRequestDto.builder()
                                .email("testUser@test.com")
                                .password("1234")
                                .name("testUser")
                                .social(Social.NoSocial).build()
                                .toEntity(passwordEncoder));
        }

        @Test
        public void Post_불러온다() throws Exception {
                List<Posts> post = createPosts(1);
                String url = "http://localhost:" + port + "/api/v1/posts/" + post.get(0).getId();

                mvc.perform(get(url))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(post.get(0).getId()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(post.get(0).getTitle()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(post.get(0).getContent()));

        }

        @Test
        public void PostsList_불러온다() throws Exception {
                createPosts(11);
                String url = "http://localhost:" + port + "/api/v1/posts";

                mvc.perform(get(url))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.totalPage").value(2))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.page").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.postsList.[0].title").value("title0"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.postsList.[0].content").value("content0"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.postsList.[9].title").value("title9"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.postsList.[9].content").value("content9"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.postsList.length()").value(10));
        }

        @Test
        public void PostsList_검색한다() throws Exception {
                createPosts(11); // title0, content0 ~ title10, content10 post가 만들어진다.
                String url = "http://localhost:" + port + "/api/v1/posts" + "?title=2&content=2"; // 하나가 검색되야 한다.

                mvc.perform(get(url))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.totalPage").value(1))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.page").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.postsList.[0].title").value("title2"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.postsList.[0].content").value("content2"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.postsList.length()").value(1));

        }

        @Test
        @WithMockCustomUser
        public void Posts_등록된다() throws Exception {
                String title = "title";
                String content = "content";
                List<Tag> tags = createTags(1, 10);

                PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                                .title(title)
                                .content(content)
                                .tagDto(tags.stream().map(e -> new TagDto(e)).collect(Collectors.toList()))
                                .build();

                String url = "http://localhost:" + port + "/api/v1/posts";

                mvc.perform(post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(requestDto)))
                                .andExpect(status().isOk());

                List<Posts> all = postsRepository.findAll();
                List<PostTag> allPostTag = postTagRepository.findAllByPostsId(all.get(0).getId());
                assertThat(all.get(0).getTitle()).isEqualTo(title);
                assertThat(all.get(0).getContent()).isEqualTo(content);
                allPostTag.forEach(pt -> {
                        assertThat(tags.stream().filter(t -> pt.getTag().getName().equals(t.getName()))
                                        .findAny().orElse(null)).isNotEqualTo(null);
                });
        }

        @Test
        @WithMockCustomUser
        void Posts_수정된다() throws Exception {
                List<Tag> oldTag = createTags(1, 2);

                Posts tempPost = Posts.builder().title("title").content("content").build();
                List<PostTag> postTag = oldTag.stream()
                                                .map(ot -> PostTag.builder().posts(tempPost).tag(ot).build())
                                                .collect(Collectors.toList());
                tempPost.updateTags(postTag);
                Posts savedPosts = postsRepository.save(tempPost);
                
                Long updateId = savedPosts.getId();
                String updatedTitle = "title-update";
                String updatedContent = "content-update";
                oldTag.remove(0);
                List<Tag> newTag = createTags(3, 4);
                newTag.addAll(oldTag);

                PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                                .title(updatedTitle)
                                .content(updatedContent)
                                .tags(newTag.stream().map(nt -> new TagDto(nt)).collect(Collectors.toList()))
                                .build();

                String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

                mvc.perform(put(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(requestDto)))
                                .andExpect(status().isOk());

                List<Posts> all = postsRepository.findAll();
                assertThat(all.get(0).getTitle()).isEqualTo(updatedTitle);
                assertThat(all.get(0).getContent()).isEqualTo(updatedContent);
                List<PostTag> allPostTag = postTagRepository.findAllByPostsId(all.get(0).getId());
                allPostTag.forEach(pt -> {
                        assertThat(newTag.stream().filter(t -> pt.getTag().getName().equals(t.getName()))
                                        .findAny().orElse(null)).isNotEqualTo(null);
                });
        }

        @Test
        public void BaseTimeEntity_등록() {
                LocalDateTime now = LocalDateTime.of(2022, 2, 8, 0, 0, 0);
                postsRepository.save(Posts.builder().title("title").content("content").build());

                List<Posts> postsList = postsRepository.findAll();

                Posts posts = postsList.get(0);
                assertThat(posts.getCreatedDate()).isAfter(now);
                assertThat(posts.getModifiedDate()).isAfter(now);

        }

        private List<Posts> createPosts(int postsNum) {
                String baseTitle = "title";
                String baseContent = "content";
                List<Posts> result = new ArrayList<>();
                for (int i = 0; i < postsNum; i++) {
                        result.add(Posts.builder()
                                        .title(baseTitle + Integer.toString(i))
                                        .content(baseContent + Integer.toString(i))
                                        .author(testMember)
                                        .build());
                }
                postsRepository.saveAll(result);
                return result;
        }

        private List<Tag> createTags(int start, int end){
                String baseTagName = "tag";
                List<Tag> tags = new ArrayList<>();
                IntStream.rangeClosed(start, end).forEach(i -> {
                        tags.add(Tag.builder().name(baseTagName + i).build());
                });
                tagRepository.saveAll(tags);
                return tags;
        }
}
