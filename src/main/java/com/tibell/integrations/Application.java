package com.tibell.integrations;

import com.tibell.integrations.message.CommandMessage;
import com.tibell.integrations.service.MessageSendingService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private MessageSendingService textSendingService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting CommandLineRunner!");
        Thread.sleep(4*1000);

        log.info("Sending text!");
        textSendingService.sendText("Hello, World!");
        Thread.sleep(4*1000);

        log.info("Sending command!");
        textSendingService.sendCommand(CommandMessage.Command.START, new JSONObject("{\"start\":\"true\"}"));
        Thread.sleep(4*1000);

        log.info("Sending error!");
        textSendingService.sendError(new ClassNotFoundException("This is a test exception!"));
        Thread.sleep(4*1000);

        log.info("Sending text!");
        textSendingService.sendText("This is the last message!");
        log.info("Done CommandLineRunner!");

        log.info("Sending command!");
        textSendingService.sendCommand(CommandMessage.Command.STOP, new JSONObject("{\"start\":\"false\"}"));
    }
}
