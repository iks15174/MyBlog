package com.jiho.board.springbootaws.web;

import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.service.posts.PostsService;
import com.jiho.board.springbootaws.web.dto.common.PageResultDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsResponseDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsSaveRequestDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsTagResultDto;
import com.jiho.board.springbootaws.web.dto.posts.PostsUpdateRequestDto;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping("/api/v1/posts")
    public ResponseEntity<Long> save(@RequestBody PostsSaveRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postsService.save(requestDto));
    }

    @GetMapping("/api/v1/posts")
    public ResponseEntity<PageResultDto<PostsTagResultDto, Posts>> getList(
            @RequestParam(value = "type", required = false, defaultValue = "") String type,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "category", required = false, defaultValue = "") ArrayList<Long> categoryIds,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok().body(postsService.getList(type, keyword, categoryIds, pageable));
    }

    @GetMapping("/api/v1/posts/{id}")
    public ResponseEntity<PostsResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(postsService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping("/api/v1/posts/{id}")
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {
        return ResponseEntity.ok().body(postsService.update(id, requestDto));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("/api/v1/posts/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        postsService.delete(id);
        return ResponseEntity.ok().body("Successfully delete post");
    }
}
