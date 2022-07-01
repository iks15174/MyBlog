package com.jiho.board.springbootaws.web.dto.posts;

import java.util.List;

import com.jiho.board.springbootaws.web.dto.common.TagDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PostsUpdateRequestDto {
    private String title;
    private String content;
    private List<TagDto> tags;
}
