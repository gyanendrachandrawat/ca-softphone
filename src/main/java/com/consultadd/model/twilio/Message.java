package com.consultadd.model.twilio;

import com.twilio.rest.api.v2010.account.Message.Direction;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {
    private String from;
    private String to;
    private String messageSID;
    private String body;
    private ZonedDateTime dateSent;
    private ZonedDateTime dateUpdated;
    private Direction direction;

    public static Message fromMessage(com.twilio.rest.api.v2010.account.Message message) {
        return Message.builder()
                .from(message.getFrom().toString())
                .to(message.getTo())
                .messageSID(message.getSid())
                .body(message.getBody())
                .dateSent(message.getDateSent())
                .dateUpdated(message.getDateUpdated())
                .direction(message.getDirection())
                .build();
    }
}
