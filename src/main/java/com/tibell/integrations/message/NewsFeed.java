package com.tibell.integrations.message;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Slf4j
@Value
public class NewsFeed {
    private final String title;
    private final String description;
    private final String link;
    private final Date pubDate;
    private final List<String> category;
    private final String titleEx;
    private final String etag;

    public NewsFeed(String title, String description, String link, Date pubDate, List<String> category, String titleEx) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.category = category;
        this.titleEx = titleEx;
        this.etag = NewsFeed.calcETAG(title, link);
        log.info("Etag: {}", this.etag);
    }

    public static String calcETAG(String title, String link) {
        return "" +
                Integer.toHexString(title.hashCode()) +
                Integer.toHexString(link.hashCode());
    }
}
