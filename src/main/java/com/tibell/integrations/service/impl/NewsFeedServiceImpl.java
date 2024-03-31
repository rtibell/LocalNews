package com.tibell.integrations.service.impl;

import com.tibell.integrations.mapper.NewsFeedMapper;
import com.tibell.integrations.message.NewsFeed;
import com.tibell.integrations.repository.NewsFeedRepository;
import com.tibell.integrations.service.NewsFeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NewsFeedServiceImpl implements NewsFeedService {
    //@Autowired(required = true)
    private NewsFeedRepository newsFeedRepository;

    //@Autowired
    private NewsFeedMapper newsFeedMapper;

    public NewsFeedServiceImpl(NewsFeedRepository newsFeedRepository, NewsFeedMapper newsFeedMapper) {
        this.newsFeedRepository = newsFeedRepository;
        this.newsFeedMapper = newsFeedMapper;
    }

    @Override
    public Boolean checkExists(String etag) {
        return newsFeedRepository.existsByEtag(etag);
    }

    @Override
    public Boolean checkUnique(NewsFeed newsFeed) { return newsFeedRepository.findByEtag2(newsFeed.getEtag2()).size() <= 1; } // Records is saved before this test.


    @Override
    public Boolean saveNewsFeed(NewsFeed newsFeed) {
        if (checkExists(newsFeed.getEtag())) {
            return Boolean.FALSE;
        }
        log.info("Saving NewsFeed: {}", newsFeed.toString());
        newsFeedRepository.save(newsFeedMapper.toNewsFeedEntity(newsFeed));
        return Boolean.TRUE;
    }
}
