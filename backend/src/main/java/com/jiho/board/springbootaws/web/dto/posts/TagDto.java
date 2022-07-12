package com.jiho.board.springbootaws.web.dto.posts;

import com.jiho.board.springbootaws.domain.tag.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TagDto {
    private Long id;
    private String name;

    public TagDto(Tag entity) { 
        this.id = entity.getId();
        this.name = entity.getName();
    }
    
    public Tag toEntity() {
        return Tag.builder().name(this.name)
                    .id(this.id).build();
    }
}
