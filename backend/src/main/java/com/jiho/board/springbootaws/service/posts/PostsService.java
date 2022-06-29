package com.jiho.board.springbootaws.service.posts;

import java.util.function.Function;

import javax.transaction.Transactional;

import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.domain.posts.PostsRepository;
import com.jiho.board.springbootaws.exception.exceptions.CustomBasicException;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;
import com.jiho.board.springbootaws.service.member.dto.AuthMemberDto;
import com.jiho.board.springbootaws.web.dto.common.PageResultDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsResponseDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsSaveRequestDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsTagResultDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsUpdateRequestDto;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostsService{
    private final PostsRepository postsRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        AuthMemberDto memberDto = (AuthMemberDto) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Member author = memberRepository
                .findByEmailAndSocial(memberDto.getUsername(), memberDto.getSocial())
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_USER));

        return postsRepository.save(requestDto.addMember(author).toEntity()).getId();
    }

    @Transactional
    public PageResultDto<PostsTagResultDto, PostsTagResultDto> getList(String type, String keyword, String category, Pageable pageable) {
        Function<PostsTagResultDto, PostsTagResultDto> fn = (result -> result);
        return new PageResultDto<PostsTagResultDto, PostsTagResultDto>(postsRepository.searchPost(type, keyword, category, pageable), fn);
    }

    @Transactional
    public PostsResponseDto findById(Long id) {
        Posts entity = postsRepository.findById(id)
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_POST));
        return new PostsResponseDto(entity);
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts entity = postsRepository.findById(id)
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_POST));
        entity.update(requestDto.getTitle(), requestDto.getContent());
        return id;
    }
}
