package com.jiho.board.springbootaws.web.dto.posts;

import java.util.ArrayList;
import java.util.List;

import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.web.dto.common.PageInfoDto;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class PostsListResponseDto {
    private List<PostsResponseDto> postsList;
    private PageInfoDto pageInfo;

    public PostsListResponseDto(Page<Posts> pagableEntity) {
        this.postsList = new ArrayList<>();
        for (Posts entity : pagableEntity.getContent()) {
            this.postsList.add(new PostsResponseDto(entity));
        }
        this.pageInfo = new PageInfoDto(pagableEntity);
    }
}
