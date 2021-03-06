package com.jiho.board.springbootaws.web;

import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jiho.board.springbootaws.domain.member.MemberRole;
import com.jiho.board.springbootaws.service.tag.TagService;
import com.jiho.board.springbootaws.web.dto.tag.TagResponseDto;
import com.jiho.board.springbootaws.web.dto.tag.TagSaveRequestDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequiredArgsConstructor
@RestController
public class TagApiController {
    private final TagService tagService;

    @GetMapping("/api/v1/tags")
    public List<TagResponseDto> getTagList(@RequestParam(value = "name", required = false, defaultValue = "") String name) {
        return tagService.getList(name);
    }

    @Secured(MemberRole.ROLES.USER)
    @PostMapping("/api/v1/tags")
    public Long tagSave(@RequestBody TagSaveRequestDto requestDto) {
        return tagService.save(requestDto);
    }

    @Secured(MemberRole.ROLES.ADMIN)
    @PutMapping("/api/v1/tags/{id}")
    public Long update(@PathVariable Long id, @RequestBody TagSaveRequestDto requestDto){
        return tagService.update(id, requestDto);
    }

    
}
