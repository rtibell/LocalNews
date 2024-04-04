package com.tibell.integrations.service;

import com.tibell.integrations.event.NewsFeedEvent;
import com.tibell.integrations.message.NewsFeed;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

public interface KafkaNewsFeedService {
    public void sendNewsFeedToKafka(NewsFeedEvent newsFeed);
    public void receiveNewsFeedFromKafka(ConsumerRecord<String, NewsFeedEvent> record, Acknowledgment acknowledgment);
}
