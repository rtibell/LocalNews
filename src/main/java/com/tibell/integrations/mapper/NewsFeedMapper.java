package com.tibell.integrations.mapper;

import com.tibell.integrations.entity.NewsCategoryEntity;
import com.tibell.integrations.entity.NewsFeedEntity;
import com.tibell.integrations.event.NewsFeedEvent;
import com.tibell.integrations.message.NewsFeed;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public abstract class NewsFeedMapper {
    public static NewsFeedMapper INSTANCE = Mappers.getMapper(NewsFeedMapper.class);

    /*
    ** This method maps NewsFeed messages into NewsFeedEntity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category", qualifiedByName = "mapStringListToCategoryEntity")
    public abstract NewsFeedEntity toNewsFeedEntity(NewsFeed newsFeed);

    @InheritInverseConfiguration
    @Mapping(target = "category", source = "category", qualifiedByName = "mapCategoryEntityToStringList")
    public abstract NewsFeed toNewsFeed(NewsFeedEntity newsFeedEntity);


    //@IterableMapping(elementTargetType = String.class)
    @Named("mapCategoryEntityToStringList")
    public List<String> mapCategoryEntityToStringList(List<NewsCategoryEntity> categoryList) {
        List<String> list = new ArrayList<>();
        for (NewsCategoryEntity category : categoryList) {
            list.add(category.getCategory());
        }
        return list;
    }

    @InheritInverseConfiguration
    @Named("mapStringListToCategoryEntity")
    public List<NewsCategoryEntity> mapStringListToCategoryEntity(List<String> childSource) {
        List<NewsCategoryEntity> list = new ArrayList<>();
        for (String category : childSource) {
            list.add(new NewsCategoryEntity(category));
        }
        return list;
    }

    //    NewsFeed toDto(NewsFeedEntity source);
//
//     String mapNewsCategoryEntityToString(NewsCategoryEntity child);

}
