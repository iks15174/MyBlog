package com.jiho.board.springbootaws.web.dto.posts;

import java.util.List;
import java.util.stream.Collectors;

import com.jiho.board.springbootaws.domain.category.Category;
import com.jiho.board.springbootaws.domain.member.Member;
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

    public Posts toEntity(Member author, Category category) {
        List<PostTag> postTags = toPostTagEntity();
        Posts post = new Posts(this.title, this.content, author, category, postTags);
        return post;
    }

    private List<PostTag> toPostTagEntity() {
        return this.tagDto.stream()
                .map(t -> t.toEntity())
                .map(ten -> PostTag.builder().tag(ten).build())
                .collect(Collectors.toList());
    }
}
