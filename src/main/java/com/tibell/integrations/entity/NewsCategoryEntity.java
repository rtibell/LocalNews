package com.tibell.integrations.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Data
@Entity
@Table(name = "news_category", indexes = {@Index(columnList = "category")})
@NoArgsConstructor
@AllArgsConstructor
public class NewsCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String category;
    @ManyToOne(fetch = FetchType.LAZY)
    private NewsFeedEntity newsFeed;

    public NewsCategoryEntity(String category) {
        this.category = category;
    }

//    @Override
//    public String toString() {
//        return category;
//    }
}
