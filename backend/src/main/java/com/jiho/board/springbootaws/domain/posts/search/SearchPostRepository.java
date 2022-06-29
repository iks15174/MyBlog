package com.jiho.board.springbootaws.domain.posts.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.jiho.board.springbootaws.web.dto.posts.PostsTagResultDto;

public interface SearchPostRepository {
    PageImpl<PostsTagResultDto> searchPost(String type, String keyword, String category, String[] tags, Pageable pageable);
}
