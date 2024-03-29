package com.tibell.integrations.mapper;

import com.tibell.integrations.entity.NewsFeedEntity;
import com.tibell.integrations.message.NewsFeed;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NewsFeedMapper {
    public NewsFeedMapper INSTANCE = Mappers.getMapper(NewsFeedMapper.class);

    @Mapping(target = "id", ignore = true)
    //@Mapping(target = "id", ignore = true)
    NewsFeedEntity toNewsFeedEntity(NewsFeed newsFeed);

    @InheritInverseConfiguration
    NewsFeed toNewsFeed(NewsFeedEntity newsFeedEntity);
}
