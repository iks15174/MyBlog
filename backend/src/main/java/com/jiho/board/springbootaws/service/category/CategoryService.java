package com.jiho.board.springbootaws.service.category;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.jiho.board.springbootaws.domain.category.Category;
import com.jiho.board.springbootaws.domain.category.CategoryRepository;
import com.jiho.board.springbootaws.exception.exceptions.CustomBasicException;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;
import com.jiho.board.springbootaws.web.dto.category.CategoryResponseDto;
import com.jiho.board.springbootaws.web.dto.category.CategorySaveRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Long save(CategorySaveRequestDto requestDto) {
        if (categoryRepository.existsByName(requestDto.getName())) {
            throw new CustomBasicException(ErrorCode.CATEGORY_DUPLICATED_ERROR);
        }
        Category parent = null;
        if (!requestDto.getIsParent()) {
            parent = categoryRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEXIST_CATEGORY_ERROR));
        }
        return categoryRepository.save(requestDto.toEntity(parent)).getId();
    }

    @Transactional
    public List<CategoryResponseDto> getList() {
        List<Object[]> entities = categoryRepository.findAllWithPostCnt();
        return entities.stream().map(entity -> {
            CategoryResponseDto responseDto = new CategoryResponseDto((Category) entity[0]);
            responseDto.setPostCnt((Long) entity[1]);
            return responseDto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public List<CategoryResponseDto> getSubCategoryList() {
        List<Category> entities = categoryRepository.findAllSubCategory();
        return entities.stream().map(entity -> new CategoryResponseDto(entity)).collect(Collectors.toList());
    }

    @Transactional
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEXIST_CATEGORY_ERROR));
    }
}
