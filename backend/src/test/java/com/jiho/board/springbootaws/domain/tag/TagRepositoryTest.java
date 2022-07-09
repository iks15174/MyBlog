package com.jiho.board.springbootaws.domain.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TagRepositoryTest {
    @Autowired
    TagRepository tagRepository;

    @AfterEach
    public void cleanup() {
        tagRepository.deleteAll();
    }

    @Test
    public void 모든태그가져오기() {
        List<Tag> tags = createTags(1, 10);
        List<Tag> result = tagRepository.findAllByName("");
        tags.stream().forEach(expectedTag -> {
            assertThat(expectedTag.getName()).isIn(result.stream().map(r -> r.getName()).collect(Collectors.toList()));
        });
    }

    @Test
    public void 태그이름으로_검색하기() {
        List<Tag> tags = createTags(1, 10);
        List<Tag> result = tagRepository.findAllByName("1");
        List<Tag> tagsWithOne = tags.stream().filter(t -> t.getName().contains("1")).collect(Collectors.toList());

        tagsWithOne.stream().forEach(expectedTag -> {
            assertThat(expectedTag.getName()).isIn(result.stream().map(r -> r.getName()).collect(Collectors.toList()));
        });
    }

    private List<Tag> createTags(int start, int end) {
        String baseTagName = "tag";
        List<Tag> tags = new ArrayList<>();
        IntStream.rangeClosed(start, end).forEach(i -> {
            tags.add(Tag.builder().name(baseTagName + i).build());
        });
        return tagRepository.saveAll(tags);
    }
}
