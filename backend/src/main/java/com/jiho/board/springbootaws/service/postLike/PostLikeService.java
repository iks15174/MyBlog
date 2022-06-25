package com.jiho.board.springbootaws.service.postLike;

import java.util.Optional;

import javax.transaction.Transactional;

import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.domain.postLike.PostLike;
import com.jiho.board.springbootaws.domain.postLike.PostLikeRepository;
import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.domain.posts.PostsRepository;
import com.jiho.board.springbootaws.exception.exceptions.CustomBasicException;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;
import com.jiho.board.springbootaws.service.member.dto.AuthMemberDto;
import com.jiho.board.springbootaws.web.dto.postLike.PostLikeResponseDto;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostLikeService {

    private final PostsRepository postsRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository PostLikeRepository;

    @Transactional
    public PostLikeResponseDto togglePostLike(AuthMemberDto authMemberDto, Long postsId) {
        Posts post = postsRepository.findById(postsId)
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_POST));
        Member member = memberRepository.findByEmailAndSocial(
                authMemberDto.getUsername(), authMemberDto.getSocial())
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_USER));

        PostLike postLike = PostLikeRepository.findByMemberAndPosts(member, post);
        if (postLike == null) {
            PostLikeRepository.save(PostLike.builder()
                    .member(member).posts(post).build());
            return PostLikeResponseDto.builder().liked(true).build();
        } else {
            PostLikeRepository.delete(postLike);
            return PostLikeResponseDto.builder().liked(false).build();
        }

    }
}
