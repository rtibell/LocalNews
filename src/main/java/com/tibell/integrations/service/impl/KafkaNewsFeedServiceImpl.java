package com.tibell.integrations.service.impl;

import com.tibell.integrations.event.NewsFeedEvent;
import com.tibell.integrations.message.NewsFeed;
import com.tibell.integrations.service.KafkaNewsFeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaNewsFeedServiceImpl implements KafkaNewsFeedService {
    private KafkaTemplate<String, NewsFeedEvent> kafkaTemplate;

    @Value("${localnews.kafka.topic.name}")
    private String topic;

    @Value("${localnews.kafka.enabled:false}")
    private boolean enableKafka;

    @Autowired
    public KafkaNewsFeedServiceImpl(KafkaTemplate<String, NewsFeedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendNewsFeedToKafka(NewsFeedEvent newsFeed) {
        if (!enableKafka) {
            //log.info("Kafka is disabled, not sending NewsFeed to Kafka");
            return;
        }
        int partition = newsFeed.getLink().hashCode() % 3;
        if (partition < 0) {
            partition += 3;
        }
        log.info("Sending NewsFeed to Kafka topic {}, partition {}, key {}, payload {}", topic, partition, newsFeed.getEtag(), newsFeed);
        log.info("Event {}", newsFeed.toJson());
        kafkaTemplate.send(topic, partition, newsFeed.getEtag(), newsFeed);
        //kafkaTemplate.send(topic, 0, newsFeed.getEtag(), newsFeed.toJson());

    }
}
