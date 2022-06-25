package com.jiho.board.springbootaws.domain.comments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository<Comments, Long> {
    Page<Comments> findByPosts_Id(Long postId, Pageable pagealbe);
}
