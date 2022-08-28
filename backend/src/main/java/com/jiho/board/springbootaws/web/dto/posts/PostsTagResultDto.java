package com.jiho.board.springbootaws.web.dto.posts;

import java.util.ArrayList;
import java.util.List;

import com.jiho.board.springbootaws.domain.postTag.PostTag;
import com.jiho.board.springbootaws.domain.posts.Posts;

import lombok.Getter;

@Getter
public class PostsTagResultDto {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String category;
    private List<String> tags = new ArrayList<String>();

    public PostsTagResultDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent().getFullContent();
        this.author = entity.getAuthor().getName();
        this.category = entity.getCategory() != null ? entity.getCategory().getName() : "";
        for(PostTag t: entity.getTags()){
            this.tags.add(t.getTag().getName());
        }
    }
}
