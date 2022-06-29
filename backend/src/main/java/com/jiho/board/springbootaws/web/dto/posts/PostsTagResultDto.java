package com.jiho.board.springbootaws.web.dto.posts;

import java.util.ArrayList;
import java.util.List;

import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.domain.tag.Tag;

import lombok.Getter;

@Getter
public class PostsTagResultDto {
    private Long id;
    private String title;
    private String content;
    private String author;
    private List<String> tags = new ArrayList<String>();

    public PostsTagResultDto(Posts entity, List<Tag> tagsEntity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor().getName();
        for(Tag t:tagsEntity){
            this.tags.add(t.getName());
        }
    }
}
