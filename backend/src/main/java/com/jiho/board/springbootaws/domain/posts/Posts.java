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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.jiho.board.springbootaws.domain.BaseTimeEntity;
import com.jiho.board.springbootaws.domain.category.Category;
import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.postTag.PostTag;
import com.jiho.board.springbootaws.exception.exceptions.CustomBasicException;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;
import com.jiho.board.springbootaws.web.dto.posts.TagDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", referencedColumnName = "id")
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> tags = new ArrayList<>();

    public Posts(String title, String content, String contentType, Member author, Category category,
            List<PostTag> tags) {
        this.setTitle(title);
        this.setContent(content, contentType);
        this.setAuthor(author);
        this.setCategory(category);
        this.addTags(tags);
    }

    public Posts(String title, String content, Member author, Category category , List<PostTag> tags) {
        this(title, content, "text", author, category, tags);
    } 

    public void update(String title, String content, String contentType, List<TagDto> tagDtos, Category category) {
        this.setTitle(title);
        this.setContent(content, contentType);
        List<PostTag> removeItem = this.tags.stream()
                .filter(t -> !tagDtos.stream()
                        .filter(tds -> tds.getId() == t.getId()).findFirst().isPresent())
                .collect(Collectors.toList());
        this.deleteTags(removeItem);
        List<PostTag> addItem = tagDtos.stream()
                .filter(tds -> !this.tags.stream()
                        .filter(t -> t.getId() == tds.getId()).findFirst().isPresent())
                .map(tds -> tds.toEntity())
                .map(ten -> PostTag.builder().posts(this).tag(ten).build())
                .collect(Collectors.toList());
        this.addTags(addItem);
        this.setCategory(category);
    }

    private void setTitle(String title) {
        if (title.isEmpty()) {
            throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE);
        }
        this.title = title;
    }

    private void setContent(String content, String contentType) {
        if (content.isEmpty()) {
            throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE);
        }
        this.content = new Content(content, contentType);
    }

    private void addTags(List<PostTag> ptags) {
        ptags.forEach(pt -> {
            pt.setPost(this);
        });
        this.tags.addAll(ptags);
    }

    private void deleteTags(List<PostTag> deletedTags) {
        this.tags.removeAll(deletedTags);
    }

    private void setAuthor(Member author) {
        this.author = author;
    }

    private void setCategory(Category category) {
        if (category == null || category.getIsParent()) {
            throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE);
        }
        this.category = category;
    }
}
