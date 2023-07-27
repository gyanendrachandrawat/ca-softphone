package com.consultadd.model.twilio;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Call {
    private String from;
    private String to;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private String duration;
    private String direction;
    private String sid;
    private ZonedDateTime dateCreated;
    private ZonedDateTime dateUpdated;
    private String parentCallSid;

    public static Call fromCall(com.twilio.rest.api.v2010.account.Call call) {
        return Call.builder()
                .direction(call.getDirection())
                .to(call.getTo())
                .from(call.getFrom())
                .startTime(call.getStartTime())
                .endTime(call.getEndTime())
                .duration(call.getDuration())
                .sid(call.getSid())
                .dateCreated(call.getDateCreated())
                .dateUpdated(call.getDateUpdated())
                .parentCallSid(call.getParentCallSid())
                .build();
    }
}
