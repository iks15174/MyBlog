package com.jiho.board.springbootaws.domain.posts.search;

import java.util.ArrayList;
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
    private PasswordEncoder passwordEncoder;

    @AfterEach
    public void cleanup() {
        postTagRepository.deleteAll();
        postsRepository.deleteAll();
        tagRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @BeforeEach
    public void setup() {
        // postTagRepository.deleteAll();
        // postsRepository.deleteAll();
        // tagRepository.deleteAll();
        // memberRepository.deleteAll();
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

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Posts post = Posts.builder().title(title + i)
                    .content(content + i)
                    .author(testMember).build();
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
            System.out.println(postId + " - " + matchPost.isPresent());
            PostTag postTag = PostTag.builder().posts(matchPost.get())
                    .tag(tag).build();
            postTagRepository.save(postTag);
            tagPerPost.replace(postId, tagPerPost.get(postId) + 1);
        });

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        Page<List<Object>> postList = postsRepository.searchPost("", "", new ArrayList<Long>(), pageable);
        for (List<Object> ptd : postList.getContent()) {
            assertThat(((List<Tag>) ptd.get(1)).size()).isEqualTo(tagPerPost.get(((Posts) ptd.get(0)).getId()));
        }
        assertThat(postList.getContent().size()).isEqualTo(10);
    }

}
