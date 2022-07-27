package com.jiho.board.springbootaws.service.tag;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.jiho.board.springbootaws.domain.tag.Tag;
import com.jiho.board.springbootaws.domain.tag.TagRepository;
import com.jiho.board.springbootaws.exception.exceptions.CustomBasicException;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;
import com.jiho.board.springbootaws.web.dto.tag.TagResponseDto;
import com.jiho.board.springbootaws.web.dto.tag.TagSaveRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TagService {
    private final TagRepository tagRepository;

    @Transactional
    public List<TagResponseDto> getList(String name) {
        return tagRepository.findAllByName(name).stream().map(tagObj -> new TagResponseDto((Tag) tagObj[0], (Long)tagObj[1]))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long save(TagSaveRequestDto requestDto) {
        if(tagRepository.existsByName(requestDto.getName())){
            throw new CustomBasicException(ErrorCode.TAG_DUPLICATED_ERROR);
        }
        return tagRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, TagSaveRequestDto requestDto){
        if(requestDto.getName() == "") {
            throw new CustomBasicException(ErrorCode.INVALID_INPUT_VALUE);
        }
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_TAG));
        tag.update(requestDto.getName());
        return tag.getId();

    }

    @Transactional
    public Long delete(Long id) {
        Tag tag = tagRepository.findByIdWithPosts(id).orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_TAG));
        tagRepository.delete(tag);
        return tag.getId();

    }
}
