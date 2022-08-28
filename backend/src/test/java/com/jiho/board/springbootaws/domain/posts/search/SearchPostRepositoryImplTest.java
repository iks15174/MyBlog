package com.jiho.board.springbootaws.domain.posts.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

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
import com.jiho.board.springbootaws.web.dto.member.MemberSaveRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SearchPostRepositoryImplTest {

    private Member testMember;

    @Autowired
    PostsRepository postsRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PostTagRepository postTagRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    public void cleanup() {
        postTagRepository.deleteAll();
        postsRepository.deleteAll();
        tagRepository.deleteAll();
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
    public void 태그와함께_게시글_불러오기() {
        String title = "테스트 게시글";
        String content = "테스트 본문";
        String tagName = "태그";
        HashMap<Long, Integer> tagPerPost = new HashMap<Long, Integer>();
        List<Long> postIds = new ArrayList<>();

        Category category = createChildParentCategory(1, 1).get(0);

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Posts post = new Posts(title + i, content + i, testMember, category, Collections.emptyList());
            post = postsRepository.save(post);
            tagPerPost.put(post.getId(), 0);
            postIds.add(post.getId());
        });

        IntStream.rangeClosed(1, 30).forEach(i -> {
            int postIdIdx = (int) (Math.random() * 10);
            Long postId = postIds.get(postIdIdx);
            Optional<Posts> matchPost = postsRepository.findById(postId);
            Tag tag = Tag.builder().name(tagName + i).build();
            tagRepository.save(tag);
            PostTag postTag = PostTag.builder().posts(matchPost.get())
                    .tag(tag).build();
            postTagRepository.save(postTag);
            tagPerPost.replace(postId, tagPerPost.get(postId) + 1);
        });

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        Page<Posts> postList = postsRepository.searchPost("", "", new ArrayList<Long>(), pageable);
        for (Posts ptd : postList.getContent()) {
            assertThat(ptd.getTags().size()).isEqualTo(tagPerPost.get(ptd.getId()));
        }
        assertThat(postList.getContent().size()).isEqualTo(10);
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
