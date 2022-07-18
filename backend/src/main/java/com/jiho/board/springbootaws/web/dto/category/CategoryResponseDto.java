package com.jiho.board.springbootaws.web.dto.category;

import com.jiho.board.springbootaws.domain.category.Category;

import lombok.Getter;

@Getter
public class CategoryResponseDto {
    private Long id;
    private String name;
    private Boolean isParent;
    private Long postCnt = (long) 0;

    public CategoryResponseDto(Category entity){
        this.id = entity.getId();
        this.name = entity.getName();
        this.isParent = entity.getIsParent();
    }

    public void setPostCnt(Long postCnt){
        this.postCnt = postCnt;
    }
}
