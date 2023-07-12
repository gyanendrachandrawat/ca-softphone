package com.consultadd.model.twilio;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageRequest {
    private String from;
    private String to;
    private String body;
}
