package com.tibell.integrations.service.impl;

import com.tibell.integrations.service.KafkaAsyncReceiverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaAsyncReceiverServiceImpl implements KafkaAsyncReceiverService {
//    @Async("taskExecutor")
//    @Scheduled(fixedRate = 15000)
    @Override
    public void receiveNewsFeedFromKafka() {
//        // Magic happens here
//        log.info("Receiving NewsFeed from Kafka");
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            log.warn("Interrupted while sleeping", e);
//            throw new RuntimeException(e);
//        }
//        log.info("Received NewsFeed from Kafka");
    }
}
