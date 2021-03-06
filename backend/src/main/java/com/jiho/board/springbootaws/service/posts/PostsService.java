package com.jiho.board.springbootaws.service.posts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.jiho.board.springbootaws.domain.category.Category;
import com.jiho.board.springbootaws.domain.category.CategoryRepository;
import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.domain.posts.PostsRepository;
import com.jiho.board.springbootaws.domain.tag.Tag;
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
public class PostsService {
    private final PostsRepository postsRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        AuthMemberDto memberDto = (AuthMemberDto) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Member author = memberRepository
                .findByEmailAndSocial(memberDto.getUsername(), memberDto.getSocial())
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_USER));
        Category category = categoryRepository.findById(requestDto.getCategoryId()).orElseThrow(() -> new CustomBasicException(ErrorCode.UNEXIST_CATEGORY_ERROR));
        if(category.getIsParent()){
            throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE);
        }
        Set<Long> tagIds = requestDto.getTagDto().stream().map(t -> t.getId()).collect(Collectors.toSet());
        Integer tagCnt = tagRepository.countAllByIdIn(tagIds);
        if (tagIds.size() != tagCnt) {
            throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE); // ???????????? ?????? Tag??? ??????
        }
        return postsRepository.save(requestDto.toEntity().setAuthor(author).setCategory(category)).getId();
    }

    @Transactional
    public PageResultDto<PostsTagResultDto, List<Object>> getList(String type, String keyword, ArrayList<Long> category,
            Pageable pageable) {
        Function<List<Object>, PostsTagResultDto> fn = (result -> new PostsTagResultDto((Posts) result.get(0), (List<Tag>) result.get(1)));
        return new PageResultDto<PostsTagResultDto, List<Object>>(
                postsRepository.searchPost(type, keyword, category, pageable), fn);
    }

    @Transactional
    public PostsResponseDto findById(Long id) {
        Posts entity = postsRepository.findByIdWithTags(id)
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_POST));
        return new PostsResponseDto(entity);
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts entity = postsRepository.findByIdWithTags(id)
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_POST));
        Category category = categoryRepository.findById(requestDto.getCategoryId()).orElseThrow(() -> new CustomBasicException(ErrorCode.UNEXIST_CATEGORY_ERROR));
        if(category.getIsParent()){
            throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE);
        }
        Set<Long> tagIds = requestDto.getTags().stream().map(t -> t.getId()).collect(Collectors.toSet());
        Integer tagCnt = tagRepository.countAllByIdIn(tagIds);
        if (tagIds.size() != tagCnt) {
            throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE); // ???????????? ?????? Tag??? ??????
        }
        entity.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getTags(), category);
        return id;
    }
}
