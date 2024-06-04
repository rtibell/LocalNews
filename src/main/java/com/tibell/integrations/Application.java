package com.tibell.integrations;

import com.tibell.integrations.service.KafkaAsyncReceiverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.tibell.integrations.repository")
public class Application implements CommandLineRunner {
    @Autowired
    private Environment env;

    // Temporarily commented out
//    @Autowired
//    private KafkaAsyncReceiverService kafkaAsyncReceiverService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting CommandLineRunner!");
        // Magic happens here
        logProp("spring.jpa.hibernate.ddl-auto");
        logProp("spring.datasource.url");
        logProp("spring.datasource.username");
        logProp("spring.datasource.password");
        logProp("spring.datasource.driver-class-name");
        logProp("spring.jpa.show-sql");
        logProp("slack.callback.url");
        logProp("discord.callback.url");
        logProp("spring.kafka.bootstrap-servers");
        logProp("spring.kafka.producer.key-serializer");
        logProp("spring.kafka.producer.value-serializer");
        logProp("localnews.kafka.topic.name");
        logProp("localnews.kafka.enabled");

//        log.info("Receiving NewsFeed from Kafka");
//        kafkaAsyncReceiverService.receiveNewsFeedFromKafka();
        log.info("Done CommandLineRunner!");
    }

    private void logProp(String prop) {
        log.info(prop + ": " + env.getProperty(prop));
    }
}
