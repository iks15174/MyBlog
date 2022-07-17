package com.jiho.board.springbootaws.domain.posts.search;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface SearchPostRepository {
    PageImpl<List<Object>> searchPost(String type, String keyword, ArrayList<Long> categoryIds, Pageable pageable);
}
