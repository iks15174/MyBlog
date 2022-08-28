package com.jiho.board.springbootaws.domain.posts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Content {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String contentType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String fullContent;

    @OneToOne(mappedBy = "content")
    private Posts posts;

    public Content(String fullContent, String contentType) {
        if(contentType.isEmpty()){
            contentType = "text";
        }
        this.contentType = contentType;
        this.fullContent = fullContent;
    }
}
