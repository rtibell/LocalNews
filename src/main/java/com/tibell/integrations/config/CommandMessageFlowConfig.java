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
    public IntegrationFlow comandRoutingFlow() {
        // Routing for START, STOP, STATUS, MESSAGE, ERROR;
        log.info("Setting up ComandRoutingFlow!");
        return IntegrationFlow.from("inputChannel")
                .<CommandMessage, CommandMessage.Command>route(CommandMessage::getCommand,
                        mapping -> mapping
                                .subFlowMapping(CommandMessage.Command.MESSAGE, sf -> sf.channel("fileChannel"))
                                .subFlowMapping(CommandMessage.Command.ERROR, sf -> sf.channel("errorChannel"))
                                //.subFlowMapping(CommandMessage.Command.STATUS, sf -> sf.channel("fileChannel"))
                                //.subFlowMapping(CommandMessage.Command.START, sf -> sf.channel("fileChannel"))
                                //.subFlowMapping(CommandMessage.Command.STOP, sf -> sf.channel("fileChannel"))
                                .defaultSubFlowMapping(sf -> sf.channel("commandChannel")))
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
    public IntegrationFlow fileWriterFlow() {
        log.info("Setting up FileWriterFlow!");
        return IntegrationFlow.from("fileChannel")
                .<CommandMessage, String>transform(t -> String.format("Command: %s, Message: %s, Payload: %s", t.getCommand(), t.getMessage(), t.getPayload()))
                .handle(Files.outboundAdapter(new File("./tmp/sia5-files"))
                        .fileExistsMode(FileExistsMode.APPEND)
                        .appendNewLine(true))
                .get();
    }

    @Bean
    public IntegrationFlow errorFileWriterFlow() {
        log.info("Setting up FileWriterFlow!");
        return IntegrationFlow.from("errorChannel")
                .<CommandMessage, String>transform(t -> String.format("Command: %s, Message: %s, Payload: %s", t.getCommand(), t.getMessage(), t.getPayload()))
                .handle(Files.outboundAdapter(new File("./tmp/error-files"))
                        .fileExistsMode(FileExistsMode.APPEND)
                        .appendNewLine(true))
                .get();
    }

    @Bean
    public IntegrationFlow auditTrailFlow() {
        log.info("Setting up AuditTrailFlow!");
        return IntegrationFlow.from("commandChannel")
                .<CommandMessage, String>transform(t -> String.format("%s\t %s\t %s\t %s", t.getTimestamp(), t.getCommand(), t.getMessage(), t.getPayload()))
                .handle(Files.outboundAdapter(new File("./tmp/audit-trail"))
                        .fileExistsMode(FileExistsMode.APPEND)
                        .appendNewLine(true))
                .get();
    }

    @Bean
    public IntegrationFlow commandFlow() {
        log.info("Setting up CommandFlow!");
        return IntegrationFlow.from("commandChannel")
                .<CommandMessage, String>handle(m -> {
                    log.info("Command executor goes here! Paylod: {} Headers: {}", m.getPayload().toString(), m.getHeaders().toString());
                })
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

    @Bean
    public MessageChannel commandChannel() {
        log.info("Setting up commandChannel!");
        return new PublishSubscribeChannel();
    }
}
