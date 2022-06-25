package com.jiho.board.springbootaws.web.dto.comments;

import java.util.ArrayList;
import java.util.List;

import com.jiho.board.springbootaws.domain.comments.Comments;
import com.jiho.board.springbootaws.web.dto.common.PageInfoDto;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class CommentsListResponseDto {
    private List<CommentsResponseDto> commentsList;
    private PageInfoDto pageInfo;

    public CommentsListResponseDto(Page<Comments> pagableEntity) {
        this.commentsList = new ArrayList<>();
        for (Comments entity : pagableEntity.getContent()) {
            this.commentsList.add(new CommentsResponseDto(entity));
        }
        this.pageInfo = new PageInfoDto(pagableEntity);
    }
}
