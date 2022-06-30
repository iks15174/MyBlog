package com.jiho.board.springbootaws.domain.postTag;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;

public interface PostTagRepository extends JpaRepository<PostTag, Long>{

    @EntityGraph(attributePaths = {"tag"}, type = EntityGraphType.FETCH)
    List<PostTag> findAllByPostsId(Long id);
}
