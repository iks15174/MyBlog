package com.jiho.board.springbootaws.domain.tag;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Integer countAllByIdIn(Set<Long> tagIds);
    
}
