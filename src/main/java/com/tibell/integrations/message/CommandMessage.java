package com.tibell.integrations.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Value;
import org.json.JSONObject;

import java.time.LocalDateTime;

@Value
public class CommandMessage {
    private final Command command;
    private final String message;
    private final JSONObject payload;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public CommandMessage(Command command, String message, JSONObject payload) {
        this.command = command;
        this.message = message;
        this.payload = payload;
    }

    public enum Command {
        START, STOP, STATUS, MESSAGE, ERROR;
    }
}
