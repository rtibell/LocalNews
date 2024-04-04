package com.tibell.integrations.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * The NewsFeedEvent class represents an event in the news feed.
 * It includes properties such as title, description, link, publication date, category, etag, etag2, and source.
 * It also includes a method to convert the NewsFeedEvent object to a JSON string.
 */
@Slf4j
@JsonSerialize
@Jacksonized
@Builder
@Data
@AllArgsConstructor
public class NewsFeedEvent {
    @JsonProperty("title")
    private final String title;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("link")
    private final String link;

    @JsonProperty("pubDate")
    private final String pubDate;

    @JsonProperty("category")
    private final String titleEx;

    @JsonProperty("etag")
    private final String etag;

    @JsonProperty("etag2")
    private final String etag2;

    @JsonProperty("source")
    private final String source;


    /**
     * This method is used to convert the NewsFeedEvent object to a JSON string.
     * @return String This returns the JSON string representation of the NewsFeedEvent object.
     * @throws RuntimeException if there is a problem with JSON processing.
     */
    public String toJson() {
        String json = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
