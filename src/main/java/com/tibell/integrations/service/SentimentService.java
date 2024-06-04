package com.tibell.integrations.service;

import com.tibell.integrations.dto.Sentiment;
import reactor.core.publisher.Mono;

public interface SentimentService {
    public Sentiment fetchSentiment(String text);
}
