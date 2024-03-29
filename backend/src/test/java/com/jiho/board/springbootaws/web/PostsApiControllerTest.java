package com.jiho.board.springbootaws.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiho.board.springbootaws.domain.category.Category;
import com.jiho.board.springbootaws.domain.category.CategoryRepository;
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
import com.jiho.board.springbootaws.web.dto.member.MemberSaveRequestDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsSaveRequestDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsUpdateRequestDto;
import com.jiho.board.springbootaws.web.dto.posts.TagDto;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        private CategoryRepository categoryRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @AfterEach
        public void tearDown() throws Exception {
                postsRepository.deleteAll();
                tagRepository.deleteAll();
                memberRepository.deleteAll();
                categoryRepository.deleteAll();
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
                List<Posts> post = createPostsWithTagCategory(10);
                String url = "http://localhost:" + port + "/api/v1/posts/" + post.get(9).getId();

                mvc.perform(get(url))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(post.get(9).getId()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(post.get(9).getTitle()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(post.get(9).getContent().getFullContent()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.tags.[*].name", Matchers
                                                .containsInAnyOrder(post.get(9).getTags().stream()
                                                                .map(t -> t.getTag().getName()).toArray())));
        }

        @Test
        public void PostsList_불러온다() throws Exception {
                createPostsWithTagCategory(11);
                String url = "http://localhost:" + port + "/api/v1/posts";

                mvc.perform(get(url))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.totalPage").value(2))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.page").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].title").value("title0"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].content").value("content0"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.[9].title").value("title9"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.[9].content").value("content9"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(10));
        }

        @Test
        public void PostsList_검색한다() throws Exception {
                createPostsWithTagCategory(11); // title0, content0 ~ title10, content10 post가 만들어진다.
                String url = "http://localhost:" + port + "/api/v1/posts" + "?type=tc&keyword=2"; // 하나가 검색되야 한다.

                mvc.perform(get(url))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.totalPage").value(1))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.pageInfo.page").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].title").value("title2"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].content").value("content2"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1));

        }

        @Test
        public void PostList_TAG_CATEGORY와_함꼐_검색한다() throws Exception {
                List<Posts> posts = createPostsWithTagCategory(20);
                List<Posts> filteredPost = posts.stream()
                                .filter(p -> p.getTitle().contains("2") || p.getContent().getFullContent().contains("2"))
                                .filter(p -> p.getCategory().getId() == 4 || p.getCategory().getId() == 24)
                                .collect(Collectors.toList());
                String url = "http://localhost:" + port + "/api/v1/posts"
                                + "?type=tc&keyword=2&category=4&category=24";
                ResultActions actions = mvc.perform(get(url)).andExpect(status().isOk());
                for (int i = 0; i < filteredPost.size(); i++) {
                        actions = actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.[" + i + "].title")
                                        .value(filteredPost.get(i).getTitle()));
                        actions = actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.[" + i + "].content")
                                        .value(filteredPost.get(i).getContent()));
                        actions = actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.[" + i + "].category")
                                        .value(filteredPost.get(i).getCategory().getName()));

                }

        }

        @Test
        @WithMockCustomUser
        public void Posts_등록된다() throws Exception {
                String title = "title";
                String content = "content";
                String contentType = "text";
                List<Tag> tags = createTags(1, 10);
                Category category = createChildParentCategory(1, 1).get(0);

                PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                                .title(title)
                                .content(content)
                                .contentType(contentType)
                                .categoryId(category.getId())
                                .tagDto(tags.stream().map(e -> new TagDto(e)).collect(Collectors.toList()))
                                .build();

                String url = "http://localhost:" + port + "/api/v1/posts";

                MvcResult result = mvc.perform(post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(requestDto)))
                                .andExpect(status().isCreated())
                                .andReturn();

                Long postId = Long.parseLong(result.getResponse().getContentAsString());

                Optional<Posts> createdPost = postsRepository.findByIdWithTagsAndContent(postId);
                assertThat(createdPost.isPresent()).isTrue();
                List<PostTag> allPostTag = postTagRepository.findAllByPostsId(createdPost.get().getId());
                assertThat(createdPost.get().getTitle()).isEqualTo(title);
                assertThat(createdPost.get().getContent().getFullContent()).isEqualTo(content);
                assertThat(createdPost.get().getContent().getContentType()).isEqualTo(contentType);
                assertThat(allPostTag.size()).isEqualTo(10);
                allPostTag.forEach(pt -> {
                        assertThat(tags.stream().filter(t -> pt.getTag().getName().equals(t.getName()))
                                        .findAny().orElse(null)).isNotEqualTo(null);
                });
                assertThat(createdPost.get().getCategory().getName()).isEqualTo(category.getName());
        }

        @Test
        @WithMockCustomUser
        void Posts_수정된다() throws Exception {
                List<Tag> oldTag = createTags(1, 2);
                List<Category> categories = createChildParentCategory(1, 2);
                Category oldChildCt = categories.get(0);
                Category newChildCt = categories.get(1);
                List<PostTag> postTag = oldTag.stream()
                                .map(ot -> PostTag.builder().tag(ot).build())
                                .collect(Collectors.toList());

                Posts tempPost = new Posts("title", "content", testMember, oldChildCt, postTag);
                Posts savedPosts = postsRepository.save(tempPost);

                Long updateId = savedPosts.getId();
                String updatedTitle = "title-update";
                String updatedContent = "content-update";
                String updatedContentType = "mark-down";
                oldTag.remove(0);
                List<Tag> newTag = createTags(3, 4);
                newTag.addAll(oldTag);

                PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                                .title(updatedTitle)
                                .content(updatedContent)
                                .contentType(updatedContentType)
                                .categoryId(newChildCt.getId())
                                .tags(newTag.stream().map(nt -> new TagDto(nt)).collect(Collectors.toList()))
                                .build();

                String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

                mvc.perform(put(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(requestDto)))
                                .andExpect(status().isOk());

                Optional<Posts> updatedPost = postsRepository.findByIdWithTagsAndContent(savedPosts.getId());
                assertThat(updatedPost.isPresent()).isEqualTo(true);
                assertThat(updatedPost.get().getTitle()).isEqualTo(updatedTitle);
                assertThat(updatedPost.get().getContent().getFullContent()).isEqualTo(updatedContent);
                assertThat(updatedPost.get().getContent().getContentType()).isEqualTo(updatedContentType);
                List<PostTag> allPostTag = postTagRepository.findAllByPostsId(updatedPost.get().getId());
                allPostTag.forEach(pt -> {
                        assertThat(newTag.stream().filter(t -> pt.getTag().getName().equals(t.getName()))
                                        .findAny().orElse(null)).isNotEqualTo(null);
                });
                assertThat(updatedPost.get().getCategory().getName()).isEqualTo(newChildCt.getName());
        }

        @Test
        @WithMockCustomUser
        public void Posts_삭제된다() throws Exception {
                List<Posts> posts = createPostsWithTagCategory(10);
                Integer deletePostIdx = 4;

                String url = "http://localhost:" + port + "/api/v1/posts/" + posts.get(deletePostIdx).getId();
                mvc.perform(delete(url)).andExpect(status().isOk());
                Optional<Posts> deletedPosts = postsRepository.findById(posts.get(deletePostIdx).getId());
                assertThat(deletedPosts.isPresent()).isFalse();
        }

        @Test
        public void BaseTimeEntity_등록() {
                LocalDateTime now = LocalDateTime.of(2022, 2, 8, 0, 0, 0);
                createPostsWithTagCategory(1);
                List<Posts> postsList = postsRepository.findAll();

                Posts posts = postsList.get(0);
                assertThat(posts.getCreatedDate()).isAfter(now);
                assertThat(posts.getModifiedDate()).isAfter(now);

        }

        // i번 posts는 1 ~ i번까지의 태그와 i번 카테고리를 가진다.
        private List<Posts> createPostsWithTagCategory(int postsNum) {
                List<Posts> posts = new ArrayList<>();
                List<Tag> tags = createTags(1, postsNum);
                List<Category> categories = createChildParentCategory(1, postsNum);
                for (int i = 0; i < postsNum; i++) {
                        List<Tag> subTag = tags.subList(0, i + 1);
                        List<PostTag> postTags = new ArrayList<PostTag>();
                        subTag.forEach(st -> {
                                PostTag postTag = PostTag.builder().tag(st).build();
                                postTags.add(postTag);
                        });
                        Posts curPost = new Posts("title" + i, "content" + i, testMember, categories.get(i), postTags);
                        postsRepository.save(curPost);
                        posts.add(curPost);
                }
                return posts;
        }

        private List<Tag> createTags(int start, int end) {
                String baseTagName = "tag";
                List<Tag> tags = new ArrayList<>();
                IntStream.rangeClosed(start, end).forEach(i -> {
                        tags.add(Tag.builder().name(baseTagName + i).build());
                });
                tagRepository.saveAll(tags);
                return tags;
        }

        private List<Category> createChildParentCategory(int start, int end) {
                String parentNm = "parentCategory";
                String childNm = "childCategory";
                Boolean parent = true;

                List<Category> childCategories = new ArrayList<Category>();
                IntStream.rangeClosed(start, end).forEach(i -> {
                        Category parentCt = new Category(parentNm + i, parent, null);
                        categoryRepository.save(parentCt);
                        Category childCt = new Category(childNm + i, !parent, parentCt);
                        categoryRepository.save(childCt);
                        childCategories.add(childCt);
                });
                return childCategories;
        }
}
