package com.tibell.integrations.service.impl;

import com.tibell.integrations.dto.Sentiment;
import com.tibell.integrations.entity.NewsFeedEntity;
import com.tibell.integrations.mapper.NewsFeedEventMapper;
import com.tibell.integrations.mapper.NewsFeedMapper;
import com.tibell.integrations.message.NewsFeed;
import com.tibell.integrations.repository.NewsFeedRepository;
import com.tibell.integrations.service.KafkaNewsFeedService;
import com.tibell.integrations.service.NewsFeedService;
import com.tibell.integrations.service.SentimentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NewsFeedServiceImpl implements NewsFeedService {
    private NewsFeedRepository newsFeedRepository;
    private NewsFeedEventMapper newsFeedEventMapper;
    private NewsFeedMapper newsFeedMapper;

    // Temporarily commented out
//    @Autowired
//    private KafkaNewsFeedService kafkaNewsFeedService;

    @Autowired
    private SentimentService sentimentService;

    public NewsFeedServiceImpl(NewsFeedRepository newsFeedRepository,
                               NewsFeedMapper newsFeedMapper,
                               NewsFeedEventMapper newsFeedEventMapper) {
        this.newsFeedRepository = newsFeedRepository;
        this.newsFeedMapper = newsFeedMapper;
        this.newsFeedEventMapper = newsFeedEventMapper;
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

        // Temporarily commented out
        //kafkaNewsFeedService.sendNewsFeedToKafka(newsFeedEventMapper.toNewsFeedEvent(newsFeed));

        NewsFeedEntity entity = newsFeedMapper.toNewsFeedEntity(newsFeed);
        if (newsFeed.getTitle() != null && newsFeed.getTitle().length() > 0) {
            Sentiment titleSentiment = sentimentService.fetchSentiment(newsFeed.getTitle());
            entity.setTitleSentiment(titleSentiment.getLabel());
            entity.setTitleSentimentScore(titleSentiment.getScore());
        }

        if (newsFeed.getDescription() != null && newsFeed.getDescription().length() > 0) {
            Sentiment descriptionSentiment = sentimentService.fetchSentiment(newsFeed.getDescription());
            entity.setDescriptionSentiment(descriptionSentiment.getLabel());
            entity.setDescriptionSentimentScore(descriptionSentiment.getScore());
        }

        log.info("Saving NewsFeed: {}", newsFeed.toString());
        newsFeedRepository.save(entity);
        return Boolean.TRUE;
    }
}
