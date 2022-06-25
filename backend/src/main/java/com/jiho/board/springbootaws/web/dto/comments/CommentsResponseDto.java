package com.jiho.board.springbootaws.web.dto.comments;

import com.jiho.board.springbootaws.domain.comments.Comments;

import lombok.Getter;

@Getter
public class CommentsResponseDto {
    private Long id;
    private String content;
    private String author;

    public CommentsResponseDto(Comments entity) {
        this.id = entity.getId();
        this.content = entity.getContent();
        this.author = entity.getAuthor().getName();
    }
}
