package com.tibell.integrations.service;

import com.tibell.integrations.event.NewsFeedEvent;
import com.tibell.integrations.message.NewsFeed;

public interface KafkaNewsFeedService {
    void sendNewsFeedToKafka(NewsFeedEvent newsFeed);
}
