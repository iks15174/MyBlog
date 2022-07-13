package com.jiho.board.springbootaws.web.dto.common;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class PageResultDto<DTO, EN> {
    private List<DTO> data;
    private PageInfoDto pageInfo;
    
    public PageResultDto(Page<EN> result, Function<EN, DTO> fn){
        this.data = result.getContent().stream()
                                .map(fn).collect(Collectors.toList());
        this.pageInfo = new PageInfoDto(result);
    }
}
