package com.jiho.board.springbootaws.web;

import com.jiho.board.springbootaws.domain.member.MemberRole;
import com.jiho.board.springbootaws.service.posts.PostsService;
import com.jiho.board.springbootaws.web.dto.common.PageResultDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsResponseDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsSaveRequestDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsTagResultDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsUpdateRequestDto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class PostsApiController {
    private final PostsService postsService;

    @Secured(MemberRole.ROLES.USER)
    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto) {
        return postsService.save(requestDto);
    }

    @GetMapping("/api/v1/posts")
    public PageResultDto<PostsTagResultDto, List<Object>> getList(
            @RequestParam(value = "type", required = false, defaultValue = "") String type,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "category", required = false, defaultValue = "") ArrayList<Long> categoryIds,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return postsService.getList(type, keyword, categoryIds, pageable);
    }

    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto findById(@PathVariable Long id) {
        return postsService.findById(id);
    }

    @Secured(MemberRole.ROLES.USER)
    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {
        return postsService.update(id, requestDto);
    }
}
