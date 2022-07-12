package com.jiho.board.springbootaws.web.dto.tag;

import com.jiho.board.springbootaws.domain.tag.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TagSaveRequestDto {
    private String name;

    public Tag toEntity(){
        Tag entity = Tag.builder().name(this.name).build();
        return entity;
    }
}
