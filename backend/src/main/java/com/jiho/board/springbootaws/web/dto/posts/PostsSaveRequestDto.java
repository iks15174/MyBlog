package com.jiho.board.springbootaws.web.dto.posts;

import java.util.List;
import java.util.stream.Collectors;

import com.jiho.board.springbootaws.domain.postTag.PostTag;
import com.jiho.board.springbootaws.domain.posts.Posts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostsSaveRequestDto {
    private String title;
    private String content;
    private Long categoryId;
    private List<TagDto> tagDto;

    public Posts toEntity() {
        Posts post = Posts.builder().title(title).content(content).build();
        return post.setTags(toPostTagEntity(post));
    }

    private List<PostTag> toPostTagEntity(Posts post) {
        return this.tagDto.stream()
                .map(t -> t.toEntity())
                .map(ten -> PostTag.builder()
                        .posts(post).tag(ten).build())
                .collect(Collectors.toList());
    }
}
