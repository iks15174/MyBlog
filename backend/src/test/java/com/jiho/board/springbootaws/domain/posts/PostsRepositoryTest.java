package com.jiho.board.springbootaws.domain.posts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jiho.board.springbootaws.domain.category.Category;
import com.jiho.board.springbootaws.domain.category.CategoryRepository;
import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.domain.member.Social;
import com.jiho.board.springbootaws.web.dto.member.MemberSaveRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PostsRepositoryTest {
    private Member testMember;

    @Autowired
    PostsRepository postsRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    public void cleanup() {
        postsRepository.deleteAll();
        memberRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @BeforeEach
    public void setup() {
        testMember = memberRepository.save(MemberSaveRequestDto.builder()
                .email("testUser@test.com")
                .password("1234")
                .name("testUser")
                .social(Social.NoSocial).build()
                .toEntity(passwordEncoder));
    }

    @Test
    public void 게시글저장_불러오기() {
        String title = "테스트 게시글";
        String content = "테스트 본문";
        List<Category>  categories = createChildParentCategory(1, 1);

        postsRepository.save(new Posts(title, content, testMember, categories.get(0), Collections.emptyList()));

        List<Posts> postsList = postsRepository.findAll();

        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);

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
