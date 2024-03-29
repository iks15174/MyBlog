package com.jiho.board.springbootaws.service.posts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.jiho.board.springbootaws.domain.category.Category;
import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.postTag.PostTagRepository;
import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.domain.posts.PostsRepository;
import com.jiho.board.springbootaws.exception.exceptions.CustomBasicException;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;
import com.jiho.board.springbootaws.service.category.CategoryService;
import com.jiho.board.springbootaws.service.member.MemberService;
import com.jiho.board.springbootaws.service.tag.TagService;
import com.jiho.board.springbootaws.web.dto.common.PageResultDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsResponseDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsSaveRequestDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsTagResultDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsUpdateRequestDto;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostsService {
    private final MemberService memberService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final PostsRepository postsRepository;

    private final PostTagRepository postTagRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        Member author = memberService.getCurLoginedUser();
        Category selectedCategory = categoryService.getCategoryById(requestDto.getCategoryId());
        List<Long> tagIds = requestDto.getTagDto().stream().map(t -> t.getId()).collect(Collectors.toList());
        tagService.checkTagIds(tagIds);
        return postsRepository.save(requestDto.toEntity(author, selectedCategory)).getId();
    }

    @Transactional
    public PageResultDto<PostsTagResultDto, Posts> getList(String type, String keyword, ArrayList<Long> category,
            Pageable pageable) {
        Function<Posts, PostsTagResultDto> fn = (result -> new PostsTagResultDto(result));
        return new PageResultDto<PostsTagResultDto, Posts>(
                postsRepository.searchPost(type, keyword, category, pageable), fn);
    }

    @Transactional
    public PostsResponseDto findById(Long id) {
        Posts entity = postsRepository.findByIdWithTagsAndContent(id)
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_POST));
        return new PostsResponseDto(entity);
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts entity = postsRepository.findByIdWithTagsAndContent(id)
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_POST));
        memberService.checkCurUserIsAuthor(entity.getAuthor());
        Category selectedCategory = categoryService.getCategoryById(requestDto.getCategoryId());
        List<Long> tagIds = requestDto.getTags().stream().map(t -> t.getId()).collect(Collectors.toList());
        tagService.checkTagIds(tagIds);
        entity.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getContentType(), requestDto.getTags(),
                selectedCategory);
        return id;
    }

    @Transactional
    public void delete(Long id) {
        Posts entity = postsRepository.findByIdWithTags(id)
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_POST));
        postTagRepository.deleteAllInBatch(entity.getTags());
        postsRepository.deleteInQuery(entity.getId());
    }

    @Transactional
    public Posts getPostById(Long id) {
        return postsRepository.findById(id)
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_POST));
    }
}
