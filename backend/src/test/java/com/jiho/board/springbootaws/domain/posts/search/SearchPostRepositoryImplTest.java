package com.jiho.board.springbootaws.domain.posts.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

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
    @Transactional
    public void ???????????????_?????????_????????????() {
        String title = "????????? ?????????";
        String content = "????????? ??????";
        String tagName = "??????";
        HashMap<Long, Integer> tagPerPost = new HashMap<Long, Integer>();

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Posts post = Posts.builder().title(title + i)
                                        .content(content + i)
                                        .author(testMember).build();
            tagPerPost.put(postsRepository.save(post).getId(), 0);
        });

        IntStream.rangeClosed(1, 30).forEach(i -> {
            long postId = (long)(Math.random() * 10) + 1;
            Optional<Posts> matchPost = postsRepository.findById(postId);
            Tag tag = Tag.builder().name(tagName + i).build();
            tagRepository.save(tag);
            PostTag postTag = PostTag.builder().posts(matchPost.get())
                                                .tag(tag).build();
            postTagRepository.save(postTag);
            tagPerPost.replace(postId, tagPerPost.get(postId) + 1);
        });

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        Page<List<Object>> postList = postsRepository.searchPost("", "", new ArrayList<Long>(), pageable);
        for(List<Object> ptd: postList.getContent()){
            assertThat(((List<Tag>)ptd.get(1)).size()).isEqualTo(tagPerPost.get(((Posts)ptd.get(0)).getId()));
        }
        assertThat(postList.getContent().size()).isEqualTo(10);
    }

}
