package com.jiho.board.springbootaws.service.category;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.jiho.board.springbootaws.domain.category.CategoryRepository;
import com.jiho.board.springbootaws.exception.exceptions.CustomBasicException;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;
import com.jiho.board.springbootaws.web.dto.category.CategorySaveRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Long save(CategorySaveRequestDto requestDto){
        if(categoryRepository.existsByNameAndIsParent(requestDto.getName(), requestDto.getIsParent())){
            throw new CustomBasicException(ErrorCode.CATEGORY_DUPLICATED_ERROR);
        }
        return categoryRepository.save(requestDto.toEntity()).getId();
    }
}
