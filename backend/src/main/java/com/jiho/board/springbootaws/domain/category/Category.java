package com.jiho.board.springbootaws.domain.category;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.jiho.board.springbootaws.exception.exceptions.CustomBasicException;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32, nullable = false, unique = true)
    private String name;

    @Column
    private Boolean isParent;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    private Set<Category> subCategories = new HashSet<>();

    public Category(String name, Boolean isParent, Category parentCategory) {
        if (isParent) {
            parentNotHaveParent(parentCategory);
        } else {
            childHaveParent(parentCategory);
        }
        this.name = name;
        this.isParent = isParent;
        this.parentCategory = parentCategory;
    }

    public void update(String name, Boolean isParent, Category parentCategory) {
        if (isParent) {
            parentNotHaveParent(parentCategory);
        } else {
            childHaveParent(parentCategory);
        }
        cannotUpdateIsParent(isParent);
        this.name = name;
        this.isParent = isParent;
        this.parentCategory = parentCategory;
    }

    private void parentNotHaveParent(Category parentCt) {
        if (parentCt != null) {
            throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void childHaveParent(Category parentCt) {
        if (parentCt == null || !parentCt.getIsParent()) {
            throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void cannotUpdateIsParent(Boolean isParent) {
        if (this.isParent != isParent) {
            throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

}
