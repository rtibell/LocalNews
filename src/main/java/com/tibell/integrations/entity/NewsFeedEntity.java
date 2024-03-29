package com.tibell.integrations.entity;

import com.tibell.integrations.message.NewsFeed;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Reference;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Data
@Entity
@Table(name = "news_feed", indexes = {@Index(columnList = "etag")})
@NoArgsConstructor
public class NewsFeedEntity {
    @Id
    //@GeneratedValue(generator = "UUID")
    //@Column(name = "id", nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String title;
    private String description;
    private String link;
    private Date pubDate;
    private List<String> category;
    private String titleEx;
    @Column(nullable = false)
    private String etag;
    private String source;


    public NewsFeedEntity(String title, String description, String link, Date pubDate, List<String> category, String titleEx, String etag, String source) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.category = category;
        this.titleEx = titleEx;
        this.source = source;
        this.etag = etag;
    }
}
