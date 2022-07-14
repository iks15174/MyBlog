package com.jiho.board.springbootaws.web.dto.category;

import com.jiho.board.springbootaws.domain.category.Category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySaveRequestDto {
    private String name;
    private Boolean isParent;

    public Category toEntity() {
        return Category.builder().name(name).isParent(isParent).build();
    }
}
