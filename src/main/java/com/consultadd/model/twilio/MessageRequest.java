package com.consultadd.model.twilio;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class MessageRequest {
    private String to;
    private String body;
    private String mediaUrl;
}
