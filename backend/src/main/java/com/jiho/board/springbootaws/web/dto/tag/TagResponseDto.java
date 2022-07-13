package com.jiho.board.springbootaws.web.dto.tag;

import com.jiho.board.springbootaws.domain.tag.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponseDto {
    private Long id;
    private String name;
    private Long postCnt;

    public TagResponseDto(Tag tag, Long postCnt) {
        this.id = tag.getId();
        this.name = tag.getName();
        this.postCnt = postCnt;
    }

}
