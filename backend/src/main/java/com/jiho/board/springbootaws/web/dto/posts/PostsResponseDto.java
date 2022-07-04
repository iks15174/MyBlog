package com.jiho.board.springbootaws.web.dto.posts;

import java.util.ArrayList;
import java.util.List;

import com.jiho.board.springbootaws.domain.postTag.PostTag;
import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.web.dto.common.TagDto;

import lombok.Getter;

@Getter
public class PostsResponseDto {
    private Long id;
    private String title;
    private String content;
    private String author;
    private List<TagDto> tags = new ArrayList<TagDto>();

    public PostsResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor().getName();
        for(PostTag postTag:entity.getTags()){
            this.tags.add((new TagDto(postTag.getTag())));
        }
    }
}
