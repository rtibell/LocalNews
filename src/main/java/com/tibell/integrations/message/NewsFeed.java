package com.tibell.integrations.message;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Date;
import java.util.List;

@Value
@RequiredArgsConstructor
public class NewsFeed {
    private final String title;
    private final String description;
    private final String link;
    private final Date pubDate;
    private final List<String> category;
    private final String titleEx;

}
