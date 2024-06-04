package com.tibell.integrations.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SentimentRequest {
    private final String workload;

}
