package com.tibell.integrations.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Sentiment {
    private final String label;
    private final Float score;
}
