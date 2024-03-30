package com.tibell.integrations.repository;

import com.tibell.integrations.entity.NewsFeedEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsFeedRepository extends CrudRepository<NewsFeedEntity, UUID> {
    public Optional<NewsFeedEntity> findById(UUID uuid);
    public Optional<NewsFeedEntity> findNewsFeedByEtag(String etag);
    public List<NewsFeedEntity> findNewsFeedByEtag2(String etag);

    public List<NewsFeedEntity> findAll();
    public Boolean existsByEtag(String etag);
}
