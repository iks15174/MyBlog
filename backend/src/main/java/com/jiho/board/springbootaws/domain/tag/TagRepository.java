package com.jiho.board.springbootaws.domain.tag;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Integer countAllByIdIn(List<Long> tagIds);

    @Query("SELECT t, count(pt) " +
            "FROM Tag t left outer join PostTag pt on pt.tag = t " +
            "where t.name LIKE %:name% " +
            "group by t ")
    List<Object[]> findAllByName(@Param("name") String name);

    @Query("SELECT distinct t " +
            " from Tag t left outer join fetch t.posts p " +
            "where t.id = :id")
    Optional<Tag> findByIdWithPosts(@Param("id") Long id);

    Boolean existsByName(String name);
}
