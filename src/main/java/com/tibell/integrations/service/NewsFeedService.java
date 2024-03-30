package com.tibell.integrations.service;

import com.tibell.integrations.mapper.NewsFeedMapper;
import com.tibell.integrations.message.NewsFeed;
import com.tibell.integrations.repository.NewsFeedRepository;
import org.springframework.beans.factory.annotation.Autowired;

public interface NewsFeedService {


    public Boolean checkExists(String etag);
    public Boolean checkUnique(NewsFeed newsFeed);
    public Boolean saveNewsFeed(NewsFeed newsFeed);
}
