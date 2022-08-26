package com.jiho.board.springbootaws.service.category;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.jiho.board.springbootaws.domain.category.Category;
import com.jiho.board.springbootaws.domain.category.CategoryRepository;
import com.jiho.board.springbootaws.exception.exceptions.CustomBasicException;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;
import com.jiho.board.springbootaws.web.dto.category.CategoryResponseDto;
import com.jiho.board.springbootaws.web.dto.category.CategorySaveRequestDto;
import com.jiho.board.springbootaws.web.dto.category.CategoryUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Long save(CategorySaveRequestDto requestDto) {
        checkCreateWithName(requestDto.getName());
        Category parent = findParent(requestDto.getParentId(), requestDto.getIsParent());
        return categoryRepository.save(requestDto.toEntity(parent)).getId();
    }

    @Transactional
    public Long update(CategoryUpdateRequestDto requestDto, Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEXIST_CATEGORY_ERROR));
        checkUpdateWithName(requestDto.getName(), category);
        Category parent = findParent(requestDto.getParentId(), requestDto.getIsParent());
        category.update(requestDto.getName(), requestDto.getIsParent(), parent);
        return id;
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

    @Transactional
    private void checkCreateWithName(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new CustomBasicException(ErrorCode.CATEGORY_DUPLICATED_ERROR);
        }
    }

    @Transactional
    private void checkUpdateWithName(String newName, Category curCategory) {
        Optional<Category> sameNmCt = categoryRepository.findByName(newName);
        if(sameNmCt.isPresent() && curCategory.getId() != sameNmCt.get().getId()) {
            throw new CustomBasicException(ErrorCode.CATEGORY_DUPLICATED_ERROR);
        }
    }

    @Transactional
    private Category findParent(Long parentId, Boolean isParent) {
        Category parent = null;
        if (!isParent) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEXIST_CATEGORY_ERROR));
        }
        return parent;
    }
}
