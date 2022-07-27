package com.jiho.board.springbootaws.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jiho.board.springbootaws.domain.member.MemberRole;
import com.jiho.board.springbootaws.service.category.CategoryService;
import com.jiho.board.springbootaws.web.dto.category.CategoryResponseDto;
import com.jiho.board.springbootaws.web.dto.category.CategorySaveRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class CategoryApiController {
    private final CategoryService categoryService;

    @Secured(MemberRole.ROLES.ADMIN)
    @PostMapping("/api/v1/category")
    public ResponseEntity<Long> save(@RequestBody CategorySaveRequestDto requestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(requestDto));
    }

    @GetMapping("/api/v1/category")
    public ResponseEntity<List<CategoryResponseDto>> getList(){
        return ResponseEntity.ok().body(categoryService.getList());
    }

    @GetMapping("/api/v1/subCategory")
    public ResponseEntity<List<CategoryResponseDto>> getSubCategoryList(){
        return ResponseEntity.ok().body(categoryService.getSubCategoryList());
    }
}
