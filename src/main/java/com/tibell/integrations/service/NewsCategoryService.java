package com.tibell.integrations.service;

import com.tibell.integrations.entity.NewsCategoryEntity;

import java.util.Optional;

public interface NewsCategoryService {
    public Optional<NewsCategoryEntity> getNewsCategory(String category);

//    public void saveNewsCategory(String category);
//    public void deleteNewsCategory(String category);
//    public void updateNewsCategory(String category);
//    public void getAllNewsCategory();
}
