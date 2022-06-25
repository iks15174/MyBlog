package com.jiho.board.springbootaws.web.dto.comments;

import com.jiho.board.springbootaws.domain.comments.Comments;
import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.posts.Posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentsSaveRequestDto {
    private String content;
    private Long postsId;

    @Builder
    public CommentsSaveRequestDto(String content, Long postsId) {
        this.content = content;
        this.postsId = postsId;
    }

    public Comments toEntity(Posts posts, Member author) {
        return Comments.builder().content(this.content).author(author).posts(posts).build();
    }
}
