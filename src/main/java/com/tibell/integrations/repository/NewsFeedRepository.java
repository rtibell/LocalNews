package com.tibell.integrations.repository;

import com.tibell.integrations.entity.NewsFeedEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This is the NewsFeedRepository interface. It extends CrudRepository and provides methods for CRUD operations on NewsFeedEntity objects.
 * The NewsFeedEntity objects are identified by UUID.
 */
public interface NewsFeedRepository extends CrudRepository<NewsFeedEntity, UUID> {

    /**
     * This method is used to find a NewsFeedEntity by its UUID.
     * @param uuid This is the UUID of the NewsFeedEntity to be found.
     * @return Optional<NewsFeedEntity> This returns an Optional that contains the NewsFeedEntity if found, or empty if not.
     */
    public Optional<NewsFeedEntity> findById(UUID uuid);

    /**
     * This method is used to find a NewsFeedEntity by its etag.
     * @param etag This is the etag of the NewsFeedEntity to be found.
     * @return Optional<NewsFeedEntity> This returns an Optional that contains the NewsFeedEntity if found, or empty if not.
     */
    public Optional<NewsFeedEntity> findByEtag(String etag);

    /**
     * This method is used to find a list of NewsFeedEntity by its etag.
     * @param etag This is the etag of the NewsFeedEntity to be found.
     * @return List<NewsFeedEntity> This returns a list of NewsFeedEntity that match the provided etag.
     */
    public List<NewsFeedEntity> findByEtag2(String etag);

    /**
     * This method is used to find all NewsFeedEntity.
     * @return List<NewsFeedEntity> This returns a list of all NewsFeedEntity.
     */
    public List<NewsFeedEntity> findAll();

    /**
     * This method is used to check if a NewsFeedEntity exists by its etag.
     * @param etag This is the etag of the NewsFeedEntity to be checked.
     * @return Boolean This returns true if a NewsFeedEntity with the provided etag exists, false otherwise.
     */
    public Boolean existsByEtag(String etag);
}
