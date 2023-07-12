package com.consultadd.service;

import com.consultadd.Identity;
import com.consultadd.model.twilio.Message;
import com.consultadd.model.twilio.MessageRequest;
import com.twilio.base.ResourceSet;
import com.twilio.http.TwilioRestClient;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired private Identity identities;
    @Autowired TwilioRestClient twilioRestClient;

    public Set<Message> listAllMessage(String from) {
        ResourceSet<com.twilio.rest.api.v2010.account.Message> sentmessages =
                com.twilio.rest.api.v2010.account.Message.reader()
                        .setFrom(from)
                        .read(twilioRestClient);

        ResourceSet<com.twilio.rest.api.v2010.account.Message> receivedMessages =
                com.twilio.rest.api.v2010.account.Message.reader()
                        .setTo(from)
                        .read(twilioRestClient);
        Set<Message> response =
                new TreeSet<>(
                        Comparator.comparing(Message::getDateSent, Comparator.reverseOrder()));
        for (com.twilio.rest.api.v2010.account.Message message : sentmessages) {
            response.add(Message.fromMessage(message));
        }
        for (com.twilio.rest.api.v2010.account.Message message : receivedMessages) {
            response.add(Message.fromMessage(message));
        }
        return response;
    }

    public Message sendMessage(MessageRequest request) {
        com.twilio.rest.api.v2010.account.Message message =
                com.twilio.rest.api.v2010.account.Message.creator(
                                new com.twilio.type.PhoneNumber(request.getTo()),
                                new com.twilio.type.PhoneNumber(request.getFrom()),
                                request.getBody())
                        .create(twilioRestClient);

        return Message.fromMessage(message);
    }
}
