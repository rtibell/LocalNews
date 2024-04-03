package com.tibell.integrations.repository;

import com.tibell.integrations.entity.NewsCategoryEntity;
import com.tibell.integrations.entity.NewsFeedEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface NewsCategoryRepository extends CrudRepository<NewsCategoryEntity, UUID> {
    public Optional<NewsCategoryEntity> findById(UUID uuid);
    public Optional<NewsCategoryEntity> findByCategory(String category);
}
