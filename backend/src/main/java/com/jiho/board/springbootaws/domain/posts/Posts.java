package com.jiho.board.springbootaws.domain.posts;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.jiho.board.springbootaws.domain.BaseTimeEntity;
import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.postTag.PostTag;
import com.jiho.board.springbootaws.web.dto.common.TagDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Builder
@ToString
@Getter
@NoArgsConstructor
@Entity
public class Posts extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="posts", cascade = CascadeType.ALL)
    private List<PostTag> tags = new ArrayList<>();

    public void update(String title, String content, List<TagDto> tags) {
        this.title = title;
        this.content = content;
        this.tags = tags.stream()
                        .map(t -> t.toEntity())
                        .map(ten -> PostTag.builder().posts(this).tag(ten).build())
                        .collect(Collectors.toList());
    }

    public void updateTags(List<PostTag> ptags) {
        this.tags = ptags;
    }
}
