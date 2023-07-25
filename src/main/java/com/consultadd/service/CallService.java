package com.consultadd.service;

import com.consultadd.Identity;
import com.consultadd.model.twilio.Call;
import com.consultadd.model.twilio.MessageRequest;
import com.consultadd.util.Constants;
import com.twilio.base.ResourceSet;
import com.twilio.http.HttpMethod;
import com.twilio.http.TwilioRestClient;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Client;
import com.twilio.twiml.voice.Dial;
import com.twilio.twiml.voice.Hangup;
import com.twilio.twiml.voice.Number;
import com.twilio.twiml.voice.Record;
import com.twilio.twiml.voice.Say;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CallService {
    @Autowired private Identity identities;
    @Autowired private TwilioRestClient twilioRestClient;

    @Autowired private MessageService messageService;

    public String createVoiceResponse(String to, String from) {
        VoiceResponse voiceTwimlResponse;

        if (to != null) {

            Dial.Builder dialBuilder =
                    new Dial.Builder().callerId(identities.getPhoneById(from.substring(7)));

            Dial.Builder dialBuilderWithReceiver = addChildReceiver(dialBuilder, to);
            dialBuilderWithReceiver.action("/call/voicemail").method(HttpMethod.POST).timeout(30);

            voiceTwimlResponse =
                    new VoiceResponse.Builder().dial(dialBuilderWithReceiver.build()).build();

        } else {
            voiceTwimlResponse =
                    new VoiceResponse.Builder()
                            .say(new Say.Builder(Constants.DEFAULT_CALL_MESSAGE).build())
                            .build();
        }

        return voiceTwimlResponse.toXml();
    }

    public String getRedirectToClientVoiceResponse(String to) {
        VoiceResponse voiceTwimlResponse;

        Say say = new Say.Builder(Constants.REDIRECT_WAIT_MESSAGE).build();
        Client client = new Client.Builder(identities.getIdentityByPhone(to)).build();

        Dial dial = new Dial.Builder().client(client).build();

        voiceTwimlResponse = new VoiceResponse.Builder().say(say).dial(dial).build();
        return voiceTwimlResponse.toXml();
    }

    private Dial.Builder addChildReceiver(Dial.Builder builder, String to) {

        if (isPhoneNumber(to)) {
            return builder.number(new Number.Builder(to).build());
        }
        return builder.client(new Client.Builder(to).build());
    }

    // validate the phone number
    private boolean isPhoneNumber(String to) {
        return to.matches("^[\\d\\+\\-\\(\\) ]+$");
    }

    public String recordVoiceMail() {
        VoiceResponse voiceTwimlResponse;

        Say say =
                new Say.Builder(
                                "Your call could not be answered at the moment. Please leave a"
                                        + " message.")
                        .build();

        Record doRecord =
                new Record.Builder()
                        .timeout(30)
                        .transcribe(true)
                        .action("/call/hangup")
                        .recordingStatusCallback("/call/handlerecordings")
                        .method(HttpMethod.POST)
                        .recordingStatusCallbackMethod(HttpMethod.POST)
                        .build();

        voiceTwimlResponse = new VoiceResponse.Builder().say(say).record(doRecord).build();
        return voiceTwimlResponse.toXml();
    }

    public String hangupCall() {
        VoiceResponse voiceTwimlResponse;

        Hangup hangup = new Hangup.Builder().build();
        voiceTwimlResponse = new VoiceResponse.Builder().hangup(hangup).build();
        return voiceTwimlResponse.toXml();
    }

    public Set<Call> listAllCalls(String from) {
        ResourceSet<com.twilio.rest.api.v2010.account.Call> sentmessages =
                com.twilio.rest.api.v2010.account.Call.reader()
                        .setFrom(from)
                        .read(twilioRestClient);

        ResourceSet<com.twilio.rest.api.v2010.account.Call> receivedMessages =
                com.twilio.rest.api.v2010.account.Call.reader().setTo(from).read(twilioRestClient);
        Set<Call> response =
                new TreeSet<>(
                        Comparator.comparing(Call::getDateCreated, Comparator.reverseOrder()));
        for (com.twilio.rest.api.v2010.account.Call call : sentmessages) {
            response.add(Call.fromCall(call));
        }
        for (com.twilio.rest.api.v2010.account.Call call : receivedMessages) {
            response.add(Call.fromCall(call));
        }
        return response;
    }

    public void handleVoiceMailRecordings(String recordingUrl, String callSid, String accountSid) {
        Call call = null;

        Iterator<com.twilio.rest.api.v2010.account.Call> childCallIterator =
                com.twilio.rest.api.v2010.account.Call.reader()
                        .setParentCallSid(callSid)
                        .limit(1)
                        .read(twilioRestClient)
                        .iterator();
        if (childCallIterator.hasNext()) {
            call = Call.fromCall(childCallIterator.next());
        }
        log.info("Call info {}", call);
        messageService.sendMessage(
                MessageRequest.builder()
                        .from(call.getFrom())
                        .to(call.getTo())
                        .body("You have a new voice mail. " + recordingUrl)
                        .build());
    }
}
