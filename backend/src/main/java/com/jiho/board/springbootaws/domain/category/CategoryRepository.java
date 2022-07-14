package com.jiho.board.springbootaws.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Boolean existsByNameAndIsParent(String name, Boolean isParent);
    
}
