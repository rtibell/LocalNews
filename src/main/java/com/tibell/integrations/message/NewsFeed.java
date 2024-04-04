package com.tibell.integrations.message;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Default;

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
    private final String etag2;
    private final String source;
    private final String ticker;

    public NewsFeed(SyndEntry entry, String source, String ticker) {
        this.title = NewsFeed.cleanUp(entry.getTitle());
        if (entry.getDescription() != null)  this.description = NewsFeed.cleanUp(entry.getDescription().getValue());
        else this.description = "";
        this.link = entry.getLink();
        this.pubDate = entry.getPublishedDate();
        if (entry.getCategories() != null) this.category = entry.getCategories().stream().map(SyndCategory::getName).toList();
        else this.category = List.of();
        if (entry.getTitleEx() != null)  this.titleEx = NewsFeed.cleanUp(entry.getTitleEx().getValue());
        else this.titleEx = "";
        this.source = source;
        this.ticker = ticker;
        this.etag = NewsFeed.calcETAG(title, link, source);
        this.etag2 = NewsFeed.calcETAG2(title);
        log.info("NewsFeed Etag: {}", this.etag);
    }

    @Default
    public NewsFeed(String title, String description, String link, Date pubDate, List<String> category, String titleEx, String source, String ticker) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.category = category;
        this.titleEx = titleEx;
        this.source = source;
        this.ticker = ticker;
        this.etag = NewsFeed.calcETAG(title, link, source);
        this.etag2 = NewsFeed.calcETAG2(title);
        log.info("Etag: {}", this.etag);
    }

    public static String calcETAG(String title, String link, String source) {
        return "" +
                Integer.toHexString(title.hashCode()) +
                Integer.toHexString(link.hashCode()) +
                Integer.toHexString(source.hashCode())
                ;
    }

    public static String calcETAG2(String title) {
        return "" + Integer.toHexString(title.hashCode());
    }

    public static String cleanUp(String s) {
        return s.replaceAll("\"", "\\\"")
                .replaceAll("<p>", "")
                .replaceAll("</p>", " ");
    }
}
