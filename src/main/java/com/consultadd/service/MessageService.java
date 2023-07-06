package com.consultadd.service;

import com.consultadd.Identity;
import com.consultadd.model.MessageRequest;
import com.twilio.base.ResourceSet;
import com.twilio.http.TwilioRestClient;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Message;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired private Identity identities;
    @Autowired TwilioRestClient twilioRestClient;

    public String createSMSResponse(String to, String body, String from) {
        MessagingResponse voiceTwimlResponse;

        Body body1 = new Body.Builder(body).build();
        Message.Builder messageBuilder =
                new Message.Builder()
                        .to(to)
                        .from(identities.getPhoneById(from.substring(7)))
                        .body(body1);

        voiceTwimlResponse =
                new MessagingResponse.Builder().message(messageBuilder.build()).build();

        return voiceTwimlResponse.toXml();
    }

    public String getRedirectToClientMessageResponse(String from, String to, String body) {
        MessagingResponse messageTwimlResponse;

        Message message =
                new Message.Builder()
                        .from(from)
                        .to(identities.getIdentityByPhone(to))
                        .body(new Body.Builder(body).build())
                        .build();

        messageTwimlResponse = new MessagingResponse.Builder().message(message).build();
        return messageTwimlResponse.toXml();
    }

    public Set<com.consultadd.model.Message> listAllMessage(String from) {
        ResourceSet<com.twilio.rest.api.v2010.account.Message> sentmessages =
                com.twilio.rest.api.v2010.account.Message.reader()
                        .setFrom(from)
                        .read(twilioRestClient);

        ResourceSet<com.twilio.rest.api.v2010.account.Message> receivedMessages =
                com.twilio.rest.api.v2010.account.Message.reader()
                        .setTo(from)
                        .read(twilioRestClient);
        Set<com.consultadd.model.Message> response =
                new TreeSet<>(
                        Comparator.comparing(
                                com.consultadd.model.Message::getDateSent,
                                Comparator.reverseOrder()));
        for (com.twilio.rest.api.v2010.account.Message message : sentmessages) {
            response.add(com.consultadd.model.Message.fromMessage(message));
        }
        for (com.twilio.rest.api.v2010.account.Message message : receivedMessages) {
            response.add(com.consultadd.model.Message.fromMessage(message));
        }
        return response;
    }

    public com.consultadd.model.Message sendMessage(MessageRequest request) {
        com.twilio.rest.api.v2010.account.Message message =
                com.twilio.rest.api.v2010.account.Message.creator(
                                new com.twilio.type.PhoneNumber(request.getFrom()),
                                new com.twilio.type.PhoneNumber(request.getTo()),
                                request.getBody())
                        .create(twilioRestClient);

        return com.consultadd.model.Message.fromMessage(message);
    }
}
