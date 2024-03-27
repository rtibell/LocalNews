package com.tibell.integrations.config;

import com.rometools.rome.feed.synd.SyndEntry;
import com.tibell.integrations.message.CommandMessage;
import com.tibell.integrations.message.NewsFeed;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.feed.inbound.FeedEntryMessageSource;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import java.io.File;
import java.net.MalformedURLException;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class CommandMessageFlowConfig {

    @Value("${slack.callback.url}")
    private String slackCallbackUrl;

    @Bean
    public IntegrationFlow rssReaderFlow() throws MalformedURLException {
        log.info("Setting up RSSReaderFlow!");
        return IntegrationFlow.from(new FeedEntryMessageSource(new UrlResource("https://www.svd.se/?service=rss"), "rss-feed-svd"), e -> e.poller(Pollers.fixedDelay(120 * 1000)))
                .<SyndEntry, NewsFeed>transform(t -> new NewsFeed(
                        t.getTitle(),
                        t.getDescription().getValue(),
                        t.getLink(),
                        t.getPublishedDate(),
                        t.getCategories().stream().map(x -> x.getName()).collect(Collectors.toList()),
                        t.getTitleEx().getValue()))
                .channel("rssChannel")
                .get();
    }

    @Bean
    public IntegrationFlow slackEnterFlow() {
        log.info("Setting up SlackEnterFlow!");
        return IntegrationFlow.from("rssChannel")
                .<NewsFeed, String>transform(t -> String.format("{ \"text\": \"%s: %s - %s\" } ", t.getPubDate().toString(), t.getTitle(), t.getDescription()))
                .handle(Http.outboundChannelAdapter(slackCallbackUrl)
                        .httpMethod(HttpMethod.POST)
                        .mappedRequestHeaders("content-type", "application/json")
                        .extractPayload(true)
                        .expectedResponseType(String.class))
                .get();
    }




    @Bean
    public IntegrationFlow rssFileWriterFlow() {
        log.info("Setting up RSSFileWriterFlow!");
        return IntegrationFlow.from("rssChannel")
                .<Object, String>transform(t -> new JSONObject(t).toString())
                .log(LoggingHandler.Level.INFO, "rss-file", m -> m.getPayload().toString())
                .handle(Files.outboundAdapter(new File("./tmp/rss-files"))
                        .fileExistsMode(FileExistsMode.APPEND)
                        .appendNewLine(true))
                .get();
    }




    @Bean
    public MessageChannel inputChannel() {
        log.info("Setting up inputChannel!");
        return new QueueChannel();
    }

    @Bean
    public MessageChannel rssChannel() {
        log.info("Setting up rssChannel!");
        return new PublishSubscribeChannel();
    }

}
