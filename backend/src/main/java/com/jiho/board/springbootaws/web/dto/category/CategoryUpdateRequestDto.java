package com.jiho.board.springbootaws.web.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryUpdateRequestDto {
    private String name;
    private Boolean isParent;
    private Long parentId;
}
