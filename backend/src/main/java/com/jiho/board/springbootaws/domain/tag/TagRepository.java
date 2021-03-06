package com.jiho.board.springbootaws.domain.tag;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Integer countAllByIdIn(Set<Long> tagIds);

    @Query("SELECT t, count(pt) " +
            "FROM Tag t left outer join PostTag pt on pt.tag = t " +
            "where t.name LIKE %:name% " +
            "group by t ")
    List<Object[]> findAllByName(@Param("name") String name);

    Boolean existsByName(String name);
}
