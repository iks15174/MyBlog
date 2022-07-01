package com.jiho.board.springbootaws.service.posts;

import java.util.function.Function;

import javax.transaction.Transactional;

import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.domain.posts.PostsRepository;
import com.jiho.board.springbootaws.domain.tag.TagRepository;
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
    private final TagRepository tagRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        AuthMemberDto memberDto = (AuthMemberDto) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Member author = memberRepository
                .findByEmailAndSocial(memberDto.getUsername(), memberDto.getSocial())
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_USER));
        requestDto.getTagDto().forEach(t -> { // 존재하지 않는 Tag id일 경우
            if(!tagRepository.existsById(t.getId())){
                throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE);
            }
        });
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
        Posts entity = postsRepository.findByIdWithTags(id)
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_POST));
        requestDto.getTags().forEach(t -> { // 존재하지 않는 Tag id일 경우
            if(!tagRepository.existsById(t.getId())){
                throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE);
            }
        });
        entity.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getTags());
        return id;
    }
}
