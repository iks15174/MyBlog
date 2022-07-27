package com.jiho.board.springbootaws.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<TagResponseDto>> getTagList(@RequestParam(value = "name", required = false, defaultValue = "") String name) {
        return ResponseEntity.ok().body(tagService.getList(name));
    }

    @Secured(MemberRole.ROLES.USER)
    @PostMapping("/api/v1/tags")
    public ResponseEntity<Long> tagSave(@RequestBody TagSaveRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.save(requestDto));
    }

    @Secured(MemberRole.ROLES.ADMIN)
    @PutMapping("/api/v1/tags/{id}")
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody TagSaveRequestDto requestDto){
        return ResponseEntity.ok().body(tagService.update(id, requestDto));
    }

    
}
