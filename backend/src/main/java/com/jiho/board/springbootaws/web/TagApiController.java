package com.jiho.board.springbootaws.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jiho.board.springbootaws.service.tag.TagService;
import com.jiho.board.springbootaws.web.dto.common.TagDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class TagApiController {
    private final TagService tagService;

    @GetMapping("/api/v1/tags")
    public List<TagDto> getTagList(@RequestParam(value = "name", required = false, defaultValue = "") String name) {
        return tagService.getList(name);
    }
}
