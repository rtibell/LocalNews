package com.tibell.integrations.config;

import com.rometools.rome.feed.synd.SyndEntry;
import com.tibell.integrations.mapper.NewsFeedEventMapper;
import com.tibell.integrations.mapper.NewsFeedMapper;
import com.tibell.integrations.message.NewsFeed;
import com.tibell.integrations.repository.NewsFeedRepository;
import com.tibell.integrations.service.NewsCategoryService;
import com.tibell.integrations.service.NewsFeedService;
import com.tibell.integrations.service.impl.NewsCategoryServiceImpl;
import com.tibell.integrations.service.impl.NewsFeedServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableIntegration
public class RSSFlowConfig {

    private Random rnd = new Random();

    @Value("${slack.callback.url}")
    private String slackCallbackUrl;

    @Value("${discord.callback.url}")
    private String discordCallbackUrl;

    @Value("${discord.callback.token}")
    private String discordCallbackToken;

    @Autowired(required = true)
    private NewsFeedService newsFeedService;

//    @Autowired(required = true)
//    private NewsCategoryService newsCategoryService;

    @Bean
    public NewsFeedMapper newsFeedMapper() {
        return NewsFeedMapper.INSTANCE;
    }

    @Bean
    public NewsFeedEventMapper newsFeedEventMapper() {
        return NewsFeedEventMapper.INSTANCE;
    }

    @Bean
    public NewsCategoryService newsCategoryService() { return new NewsCategoryServiceImpl(); }

    @Bean
    public NewsFeedService newsFeedService(NewsFeedRepository newsFeedRepository,
                                           NewsFeedMapper newsFeedMapper,
                                           NewsFeedEventMapper newsFeedEventMapper,
                                           NewsCategoryService newsCategoryService) {
        return new NewsFeedServiceImpl(newsFeedRepository, newsFeedMapper, newsFeedEventMapper, newsCategoryService);
    }

    @Bean
    public IntegrationFlow svdRSSReaderFlow() throws MalformedURLException {
        return genericRSSReaderFlow("https://www.svd.se/?service=rss", "rss-feed-svd", "SVD");
    }

    @Bean
    public IntegrationFlow dnRSSReaderFlow() throws MalformedURLException {
        return genericRSSReaderFlow("https://www.dn.se/rss/", "rss-feed-dn", "DN");
    }

    @Bean
    public IntegrationFlow expressenRSSReaderFlow() throws MalformedURLException {
        return genericRSSReaderFlow("https://feeds.expressen.se/nyheter/", "rss-feed-expressen", "Expressen");
    }

    @Bean
    public IntegrationFlow svtRSSReaderFlow() throws MalformedURLException {
        return genericRSSReaderFlow("https://www.svt.se/nyheter/rss.xml", "rss-feed-svt", "SVT");
    }

    @Bean
    public IntegrationFlow aftonbladedNewsRSSReaderFlow() throws MalformedURLException {
        return genericRSSReaderFlow("https://rss.aftonbladet.se/rss2/small/pages/sections/senastenytt/", "rss-feed-aftonbladet-news", "Aftonbladet-News");
    }

    @Bean
    public IntegrationFlow aftonbladedEntertRSSReaderFlow() throws MalformedURLException {
        return genericRSSReaderFlow("https://rss.aftonbladet.se/rss2/small/pages/sections/nojesbladet/", "rss-feed-aftonbladet-entertainment", "Aftonbladet-Entertainment");
    }

    @Bean
    public IntegrationFlow aftonbladedCultureRSSReaderFlow() throws MalformedURLException {
        return genericRSSReaderFlow("https://rss.aftonbladet.se/rss2/small/pages/sections/kultur/", "rss-feed-aftonbladet-culture", "Aftonbladet-Culture");
    }

    @Bean
    public IntegrationFlow wpPolicticsRSSReaderFlow() throws MalformedURLException {
        return genericRSSReaderFlow("https://www.di.se/rss", "rss-feed-di", "DI");
    }

    @Bean
    public IntegrationFlow nytTechRSSReaderFlow() throws MalformedURLException {
        return genericRSSReaderFlow("https://www.sydsvenskan.se/rss.xml", "rss-feed-nyt-sydsvenskan", "SydSvenskan");
    }

//    @Bean
//    public IntegrationFlow nytWorldRSSReaderFlow() throws MalformedURLException {
//        return genericRSSReaderFlow("https://rss.nytimes.com/services/xml/rss/nyt/World.xml", "rss-feed-nyt-world", "NewYorkTimes-World");
//    }

    @Bean
    public IntegrationFlow slackEnterFlow() {
        log.info("Setting up SlackEnterFlow!");
        return IntegrationFlow.from("rssChannel")
                .<NewsFeed, String>transform(t -> createSlackJsonPayload(t))
                .log(LoggingHandler.Level.INFO, "slack", m -> m.getPayload().toString())
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
    public IntegrationFlow discordEnterFlow() {
        log.info("Setting up DiscordEnterFlow!");
        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Bot " + discordCallbackToken);
        headers.put("content-type", "application/json");
        return IntegrationFlow.from("rssChannel")
                .enrichHeaders(headers)
                .<NewsFeed, String>transform(t -> createDiscordJsonPayload(t))
                .log(LoggingHandler.Level.INFO, "discord", m -> m.getPayload().toString())
                .handle(Http.outboundChannelAdapter(discordCallbackUrl)
                        .httpMethod(HttpMethod.POST)
                        .mappedRequestHeaders("Authorization", "content-type")
                        .extractPayload(true)
                        .expectedResponseType(String.class)
                )
                .get();
    }

    public String createDiscordJsonPayload(NewsFeed newsFeed) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.format("{ \"content\": \"%s\", \"tts\": false, \"embeds\": [{\"title\": \"%s\", \"description\": \"%s\", \"url\": \"%s\", \"color\": 5814783 }] } ",
                sdf.format(newsFeed.getPubDate()),
                newsFeed.getTitle(),
                newsFeed.getDescription(),
                newsFeed.getLink());
    }


    @Bean
    public IntegrationFlow rssFileWriterFlow() {
        log.info("Setting up RSSFileWriterFlow!");
        return IntegrationFlow.from("fileChannel")
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
        int rndTime = rnd.nextInt(25) + 67;
        log.info("Setting up {} RSSReaderFlow! MetaKey={} delay={}", source, metaKey, rndTime);
        return IntegrationFlow.from(new FeedEntryMessageSource(new UrlResource(url), metaKey), e -> e.poller(Pollers.fixedDelay(rndTime * 1000)))
                .<SyndEntry, NewsFeed>transform(t -> new NewsFeed(t, source))
                .enrichHeaders(h -> h.header("source", source, true)) // Add etag header to message
                .filter(newsFeedService::saveNewsFeed)
                .filter(newsFeedService::checkUnique)
                .channel("rssChannel")
                .get();
    }
}
