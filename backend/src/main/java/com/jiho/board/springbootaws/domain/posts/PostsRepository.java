package com.jiho.board.springbootaws.domain.posts;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jiho.board.springbootaws.domain.posts.search.SearchPostRepository;

import java.util.List;
import java.util.Optional;

public interface PostsRepository extends JpaRepository<Posts, Long>, SearchPostRepository {

    @Query("SELECT p FROM Posts p ORDER BY p.id DESC")
    List<Posts> findAllDesc();

    @Query("select distinct p" +
    " from Posts p left outer join fetch PostTag pt on pt.posts = p " +
    " left outer join fetch Tag t on pt.tag = t "+
    " where p.id = :id")
    Optional<Posts> findByIdWithTags(@Param("id") Long id);

    @Query("SELECT p FROM Posts p WHERE p.title LIKE %:title% OR p.content LIKE %:content%")
    Page<Posts> findAllSearch(@Param("title") String title, @Param("content") String content, Pageable pageable);

}