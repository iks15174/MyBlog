package com.jiho.board.springbootaws.domain.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Boolean existsByNameAndIsParent(String name, Boolean isParent);
    
    @Query("SELECT c, count(p) " + 
    "FROM Category c left outer join Posts p on p.category = c " + 
    "group by c ")
    List<Object[]> findAllWithPostCnt();
}
