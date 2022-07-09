package com.jiho.board.springbootaws.service.tag;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.jiho.board.springbootaws.domain.tag.TagRepository;
import com.jiho.board.springbootaws.web.dto.common.TagDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TagService {
    private final TagRepository tagRepository;

    @Transactional
    public List<TagDto> getList(String name) {
        return tagRepository.findAllByName(name).stream().map(tagEntity -> new TagDto(tagEntity))
                .collect(Collectors.toList());
    }
}
