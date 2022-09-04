package com.jiho.board.springbootaws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.jiho.board.springbootaws.domain.category.Category;
import com.jiho.board.springbootaws.domain.category.CategoryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private final CategoryRepository categoryRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!categoryRepository.findByName("base").isPresent()) {
            Category baseP = new Category("base", true, null);
            Category baseC = new Category("base sub category", false, baseP);
            categoryRepository.save(baseP);
            categoryRepository.save(baseC);
        }
    }

}
