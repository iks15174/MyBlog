package com.jiho.board.springbootaws.domain.posts.search;

import java.util.ArrayList;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.jiho.board.springbootaws.domain.posts.Posts;

public interface SearchPostRepository {
    PageImpl<Posts> searchPost(String type, String keyword, ArrayList<Long> categoryIds, Pageable pageable);
}
