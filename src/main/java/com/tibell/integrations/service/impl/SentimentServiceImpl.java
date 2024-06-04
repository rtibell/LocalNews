package com.tibell.integrations.service.impl;

import com.tibell.integrations.dto.Sentiment;
import com.tibell.integrations.dto.request.SentimentRequest;
import com.tibell.integrations.service.SentimentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SentimentServiceImpl implements SentimentService {
    private WebClient webSentimentClient;

    @Autowired
    public SentimentServiceImpl(WebClient webSentimentClient) {
        this.webSentimentClient = webSentimentClient;
    }

    @Override
    public Sentiment fetchSentiment(String text) {
        log.info("Fetching sentiment for text: {}", text);
        SentimentRequest request = new SentimentRequest(text);
        Sentiment ans =  webSentimentClient.post()
                .uri("/sentiment")
                .body(Mono.just(request), SentimentRequest.class)
                .retrieve()
                .bodyToMono(Sentiment.class)
                .block();
        if (ans != null) {
            log.info("Sentiment fetched: {} score: {}", ans.getLabel(), ans.getScore());
        }
        return ans;
    }
}
