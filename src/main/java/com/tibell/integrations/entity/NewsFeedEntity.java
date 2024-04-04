package com.tibell.integrations.entity;

import com.tibell.integrations.converter.ListToStringConverter;
import com.tibell.integrations.message.NewsFeed;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Reference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Data
@Entity
@Table(name = "news_feed", indexes = {@Index(columnList = "etag"), @Index(columnList = "etag2")})
@NoArgsConstructor
public class NewsFeedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 8192)
    private String description;

    private String link;

    private Date pubDate;

    @Convert(converter = ListToStringConverter.class)
    private List<String> category = new ArrayList<>();

    private String titleEx;

    @Column(nullable = false)
    private String etag;

    @Column(nullable = false)
    private String etag2;

    private String source;
    private String ticker;

    @Column(nullable = false)
    private Date creation_date = new Date();


    public NewsFeedEntity(String title, String description, String link, Date pubDate, List<String> category, String titleEx, String etag, String etag2, String source, String ticker) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.titleEx = titleEx;
        this.source = source;
        this.ticker = ticker;
        this.etag = etag;
        this.etag2 = etag2;
        this.category = category;
    }
}
