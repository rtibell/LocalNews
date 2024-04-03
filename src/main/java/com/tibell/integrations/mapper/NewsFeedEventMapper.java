package com.tibell.integrations.mapper;

import com.tibell.integrations.entity.NewsCategoryEntity;
import com.tibell.integrations.entity.NewsFeedEntity;
import com.tibell.integrations.event.NewsFeedEvent;
import com.tibell.integrations.message.NewsFeed;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsFeedEventMapper {
    public NewsFeedEventMapper INSTANCE = Mappers.getMapper(NewsFeedEventMapper.class);

    /*
    ** This method maps NewsFeed messages into NewsFeedEvent.
     */
    public NewsFeedEvent toNewsFeedEvent(NewsFeed newsFeed);

    @InheritInverseConfiguration
    public NewsFeed fromNewsFeedEvent(NewsFeedEvent newsFeed);

}
