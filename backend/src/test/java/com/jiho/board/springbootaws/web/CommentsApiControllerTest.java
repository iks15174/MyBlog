package com.jiho.board.springbootaws.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiho.board.springbootaws.domain.category.Category;
import com.jiho.board.springbootaws.domain.category.CategoryRepository;
import com.jiho.board.springbootaws.domain.comments.Comments;
import com.jiho.board.springbootaws.domain.comments.CommentsRepository;
import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.domain.member.Social;
import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.domain.posts.PostsRepository;
import com.jiho.board.springbootaws.util.security.WithMockCustomUser;
import com.jiho.board.springbootaws.web.dto.comments.CommentsSaveRequestDto;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentsApiControllerTest {

        private Member testMember;

        private MockMvc mvc;

        @LocalServerPort
        private int port;

        @Autowired
        private WebApplicationContext context;

        @Autowired
        private CommentsRepository commentsRepository;

        @Autowired
        private PostsRepository postsRepository;

        @Autowired
        private MemberRepository memberRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @AfterEach
        public void tearDown() throws Exception {
                commentsRepository.deleteAll();
                postsRepository.deleteAll();
                memberRepository.deleteAll();
                categoryRepository.deleteAll();
        }

        @BeforeEach
        public void setup() {
                mvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();
                // WithMockCustomUser의 정보와 일치하는 유저 생성
                testMember = memberRepository.save(MemberSaveRequestDto.builder()
                                .email("testUser@test.com")
                                .password("1234")
                                .name("testUser")
                                .social(Social.NoSocial).build()
                                .toEntity(passwordEncoder));
        }

        @Test
        @WithMockCustomUser
        public void Comments_등록된다() throws Exception {
                List<Category> categories = createChildParentCategory(1, 1);
                Posts testPosts = postsRepository
                                .save(new Posts("title", "content", testMember, categories.get(0), Collections.emptyList()));

                String content = "comment content";
                Long postsId = testPosts.getId();

                CommentsSaveRequestDto requestDto = CommentsSaveRequestDto.builder()
                                .content(content)
                                .postsId(postsId).build();

                String url = "http://localhost:" + port + "/api/v1/comments";

                mvc.perform(post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(requestDto)))
                                .andExpect(status().isCreated());

                List<Comments> all = commentsRepository.findAll();
                assertThat(all.size()).isEqualTo(1);
                assertThat(all.get(0).getContent()).isEqualTo(content);
                assertThat(all.get(0).getAuthor().getId()).isEqualTo(testMember.getId());
                assertThat(all.get(0).getPosts().getId()).isEqualTo(postsId);

        }

        private List<Category> createChildParentCategory(int start, int end) {
                String parentNm = "parentCategory";
                String childNm = "childCategory";
                Boolean parent = true;
        
                List<Category> childCategories = new ArrayList<Category>();
                IntStream.rangeClosed(start, end).forEach(i -> {
                    Category parentCt = Category.builder().name(parentNm + i).isParent(parent).build();
                    categoryRepository.save(parentCt);
                    Category childCt = Category.builder().name(childNm + i).isParent(!parent)
                            .parentCategory(parentCt)
                            .build();
                    categoryRepository.save(childCt);
                    childCategories.add(childCt);
                });
                return childCategories;
            }
}
