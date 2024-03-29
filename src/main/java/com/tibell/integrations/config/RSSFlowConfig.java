package com.tibell.integrations.config;

import com.rometools.rome.feed.synd.SyndEntry;
import com.tibell.integrations.mapper.NewsFeedMapper;
import com.tibell.integrations.message.NewsFeed;
import com.tibell.integrations.repository.NewsFeedRepository;
import com.tibell.integrations.service.NewsFeedService;
import com.tibell.integrations.service.impl.NewsFeedServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.UrlResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.feed.inbound.FeedEntryMessageSource;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableIntegration
public class RSSFlowConfig {

    @Value("${slack.callback.url}")
    private String slackCallbackUrl;

    @Autowired(required = true)
    private NewsFeedService newsFeedService;

    @Bean
    public NewsFeedMapper newsFeedMapper() {
        return NewsFeedMapper.INSTANCE;
    }
    @Bean
    public NewsFeedService newsFeedService(NewsFeedRepository newsFeedRepository, NewsFeedMapper newsFeedMapper) {
        return new NewsFeedServiceImpl(newsFeedRepository, newsFeedMapper);
    }

    @Bean
    public IntegrationFlow svdRSSReaderFlow() throws MalformedURLException {
        log.info("Setting up SVD RSSReaderFlow!", "rss-feed-svd", "SVD");
        return genericRSSReaderFlow("https://www.svd.se/?service=rss", "rss-feed-svd","SVD");
    }

    @Bean
    public IntegrationFlow aftonbladedNewsRSSReaderFlow() throws MalformedURLException {
        log.info("Setting up Aftonbladed News RSSReaderFlow!");
        return genericRSSReaderFlow("https://rss.aftonbladet.se/rss2/small/pages/sections/senastenytt/", "rss-feed-aftonbladet-news", "Aftonbladet-News");
    }

    @Bean
    public IntegrationFlow aftonbladedEntertRSSReaderFlow() throws MalformedURLException {
        log.info("Setting up Aftonbladed News RSSReaderFlow!");
        return genericRSSReaderFlow("https://rss.aftonbladet.se/rss2/small/pages/sections/nojesbladet/", "rss-feed-aftonbladet-entertainment", "Aftonbladet-Entertainment");
    }

    @Bean
    public IntegrationFlow aftonbladedCultureRSSReaderFlow() throws MalformedURLException {
        log.info("Setting up Aftonbladed News RSSReaderFlow!");
        return genericRSSReaderFlow("https://rss.aftonbladet.se/rss2/small/pages/sections/kultur/", "rss-feed-aftonbladet-culture", "Aftonbladet-Culture");
    }

    @Bean
    public IntegrationFlow slackEnterFlow() {
        log.info("Setting up SlackEnterFlow!");
        return IntegrationFlow.from("rssChannel")
                .<NewsFeed, String>transform(t -> createSlackJsonPayload(t))
                .handle(Http.outboundChannelAdapter(slackCallbackUrl)
                        .httpMethod(HttpMethod.POST)
                        .mappedRequestHeaders("content-type", "application/json")
                        .extractPayload(true)
                        .expectedResponseType(String.class))
                .get();
    }

    public String createSlackJsonPayload(NewsFeed newsFeed) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.format("{ \"text\": \"%s: %s - %s [%s]\" } ",
                sdf.format(newsFeed.getPubDate()),
                newsFeed.getTitle(),
                newsFeed.getDescription(),
                newsFeed.getLink());
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

    public IntegrationFlow genericRSSReaderFlow(String url, String metaKey, String source) throws MalformedURLException {
        return IntegrationFlow.from(new FeedEntryMessageSource(new UrlResource(url), metaKey), e -> e.poller(Pollers.fixedDelay(67 * 1000)))
//                .<SyndEntry, NewsFeed>transform(t -> new NewsFeed(
//                        t.getTitle(),
//                        t.getDescription().getValue(),
//                        t.getLink(),
//                        t.getPublishedDate(),
//                        t.getCategories().stream().map(x -> x.getName()).collect(Collectors.toList()),
//                        t.getTitleEx().getValue(),
//                        source))
                .<SyndEntry, NewsFeed>transform(t -> new NewsFeed(t, source))
                .enrichHeaders(h -> h.header("source", source, true)) // Add etag header to message
                .filter(newsFeedService::saveNewsFeed)
                .channel("rssChannel")
                .get();
    }
}
