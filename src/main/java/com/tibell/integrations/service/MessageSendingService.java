package com.tibell.integrations.service;

import com.tibell.integrations.message.CommandMessage;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
@Service
public class MessageSendingService {

    @Autowired
    private MessageChannel inputChannel;

    @Async
    public void sendText(String text) {
        log.info("Sending text: {} from thread: {}", text, Thread.currentThread().getName());
        CommandMessage cmd = new CommandMessage(CommandMessage.Command.MESSAGE, text, new JSONObject("{\"text\":\"" + text + "\"})"));
        inputChannel.send(MessageBuilder.withPayload(cmd).build());
    }

    @Async
    public void sendCommand(CommandMessage.Command command, JSONObject payload) {
        log.info("Sending command: {} with payload {} from thread: {}", command, payload.toString(), Thread.currentThread().getName());
        CommandMessage cmd = new CommandMessage(command, "issuing command", payload);
        inputChannel.send(MessageBuilder.withPayload(cmd).build());
    }

    @Async
    public void sendError(Exception ex) {
        log.info("Sending error: {} {} from thread: {}", ex.getClass().getName(), ex.getMessage(), Thread.currentThread().getName());
        HashMap<String, String> error = new HashMap<>();
        error.put("exception", ex.getClass().getName());
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now().toString());
        CommandMessage cmd = new CommandMessage(CommandMessage.Command.ERROR, ex.getMessage(), new JSONObject(error));
        inputChannel.send(MessageBuilder.withPayload(cmd).build());
    }
}
