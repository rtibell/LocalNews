package com.tibell.integrations.service.impl;

import com.tibell.integrations.entity.NewsCategoryEntity;
import com.tibell.integrations.repository.NewsCategoryRepository;
import com.tibell.integrations.service.NewsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class NewsCategoryServiceImpl implements NewsCategoryService {
    @Autowired
    private NewsCategoryRepository newsCategoryRepository;

    @Override
    public Optional<NewsCategoryEntity> getNewsCategory(String category) {
        Optional<NewsCategoryEntity> categoryEntity = newsCategoryRepository.findByCategory(category);
        return categoryEntity;
    }
}
