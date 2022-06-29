package com.jiho.board.springbootaws.web.dto.common;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class PageResultDto<DTO, EN> {
    private List<DTO> dtoList;
    private PageInfoDto pageinfo;
    
    public PageResultDto(Page<EN> result, Function<EN, DTO> fn){
        this.dtoList = result.getContent().stream()
                                .map(fn).collect(Collectors.toList());
        this.pageinfo = new PageInfoDto(result);
    }
}
