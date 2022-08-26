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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiho.board.springbootaws.domain.category.Category;
import com.jiho.board.springbootaws.domain.category.CategoryRepository;
import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.domain.member.MemberRole;
import com.jiho.board.springbootaws.domain.member.Social;
import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.domain.posts.PostsRepository;
import com.jiho.board.springbootaws.util.security.WithMockCustomUser;
import com.jiho.board.springbootaws.web.dto.category.CategorySaveRequestDto;
import com.jiho.board.springbootaws.web.dto.member.MemberSaveRequestDto;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryApiControllerTest {

        private Member testMember;

        private MockMvc mvc;

        @LocalServerPort
        private int port;

        @Autowired
        private WebApplicationContext context;

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private PostsRepository postsRepository;

        @Autowired
        private MemberRepository memberRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @AfterEach
        public void clean() throws Exception {
                postsRepository.deleteAll();
                categoryRepository.deleteAll();
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
        @WithMockCustomUser(role = MemberRole.ADMIN)
        public void Category_생성한다() throws Exception {
                String categoryName = "category";
                Boolean isParent = true;
                CategorySaveRequestDto requestDto = CategorySaveRequestDto.builder().name(categoryName)
                                .isParent(isParent)
                                .build();
                String url = "http://localhost:" + port + "/api/v1/category";

                mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(requestDto))).andExpect(status().isCreated());

                Category category = categoryRepository.findAll().get(0);
                assertThat(category.getName()).isEqualTo(categoryName);
                assertThat(category.getIsParent()).isEqualTo(isParent);

        }

        @Test
        @WithMockCustomUser(role = MemberRole.ADMIN)
        public void 중복된_Category_생성할_수_없다() throws Exception {
                String categoryName = "category";
                Boolean isParent = true;
                CategorySaveRequestDto requestDto = CategorySaveRequestDto.builder().name(categoryName)
                                .isParent(isParent)
                                .build();
                String url = "http://localhost:" + port + "/api/v1/category";

                mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(requestDto))).andExpect(status().isCreated());

                Category category = categoryRepository.findAll().get(0);
                assertThat(category.getName()).isEqualTo(categoryName);
                assertThat(category.getIsParent()).isEqualTo(isParent);

                mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(requestDto)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockCustomUser(role = MemberRole.ADMIN)
        public void 자식_Category를_생성한다() throws Exception {
                String subCategoryNm = "subCategory";
                String parentCategoryNm = "parentCategory";
                Boolean parent = true;

                Category parentCt = categoryRepository
                                .save(new Category(parentCategoryNm, parent, null));
                CategorySaveRequestDto requestDto = CategorySaveRequestDto.builder().name(subCategoryNm)
                                .isParent(!parent)
                                .parentId(parentCt.getId())
                                .build();

                String url = "http://localhost:" + port + "/api/v1/category";

                mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(requestDto))).andExpect(status().isCreated());

                Optional<Category> childCt = categoryRepository.findByIsParent(false);
                assertThat(childCt.isPresent()).isTrue();
                assertThat(childCt.get().getName()).isEqualTo(subCategoryNm);
                assertThat(childCt.get().getParentCategory().getId()).isEqualTo(parentCt.getId());
        }

        @Test
        public void Category를_PostCnt와_함께_가져온다() throws Exception {
                List<Category> categories = createCategories(5);
                for (int i = 0; i < categories.size(); i++) {
                        if (!categories.get(i).getIsParent()) {
                                createPosts(i + 1, categories.get(i));
                        }
                }
                String url = "http://localhost:" + port + "/api/v1/category";
                ResultActions actions = mvc.perform(get(url)).andExpect(status().isOk());
                for (int i = 0; i < categories.size(); i++) {
                        int postCnt = 0;
                        if(!categories.get(i).getIsParent()){
                                postCnt = i + 1;
                                continue;
                        }
                        actions = actions.andExpect(
                                        MockMvcResultMatchers.jsonPath("$.[" + i + "].name")
                                                        .value(categories.get(i).getName()));
                        actions = actions.andExpect(
                                        MockMvcResultMatchers.jsonPath("$.[" + i + "].postCnt").value(postCnt));
                }
        }

        @Test
        public void 자식_Category만_가져온다() throws Exception {
                List<Category> categories = createCategories(5);
                List<Category> subCategory = categories.stream().filter(c -> !c.getIsParent()).collect(Collectors.toList());
                String url = "http://localhost:" + port + "/api/v1/subCategory";
                ResultActions actions = mvc.perform(get(url)).andExpect(status().isOk());
                for(int i = 0; i < subCategory.size(); i++) {
                        actions = actions.andExpect(MockMvcResultMatchers.jsonPath("$.[" + i + "].isParent").value(false));
                        actions = actions.andExpect(MockMvcResultMatchers.jsonPath("$.[" + i + "].name").value(subCategory.get(i).getName()));
                }

        }

        private List<Category> createCategories(int categoryNum) {
                String baseName = "category";
                Boolean parent = true;
                List<Category> result = new ArrayList<>();
                IntStream.rangeClosed(1, categoryNum).forEach(i -> {
                        Category parentCt = new Category(baseName + i + "parent", parent, null);
                        categoryRepository.save(parentCt);
                        result.add(parentCt);
                        Category childCt = new Category(baseName + i, !parent, parentCt);
                        categoryRepository.save(childCt);
                        result.add(childCt);
                });
                return result;
        }

        private List<Posts> createPosts(int postsNum, Category category) {
                String baseTitle = "title";
                String baseContent = "content";
                List<Posts> result = new ArrayList<>();
                for (int i = 0; i < postsNum; i++) {
                        result.add(new Posts(baseTitle + i, baseContent + i, testMember, category, Collections.emptyList()));
                                        
                }
                postsRepository.saveAll(result);
                return result;
        }
}
