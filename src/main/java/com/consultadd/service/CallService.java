package com.consultadd.service;

import static com.consultadd.util.TwilioUtility.isPhoneNumber;

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
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallService {
    private final UserService userService;
    private final TwilioRestClient twilioRestClient;
    private final MessageService messageService;

    public String createVoiceResponse(String to, String from) {
        VoiceResponse voiceTwimlResponse;

        if (to != null) {

            Dial.Builder dialBuilder =
                    new Dial.Builder()
                            .callerId(
                                    userService
                                            .findByClientId(from.substring(7))
                                            .getTwilioNumber());

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

        Client client =
                new Client.Builder(userService.findByTwilioNumber(to).getClientId().toString())
                        .build();

        Dial dial = new Dial.Builder().client(client).build();

        voiceTwimlResponse = new VoiceResponse.Builder().dial(dial).build();
        return voiceTwimlResponse.toXml();
    }

    private Dial.Builder addChildReceiver(Dial.Builder builder, String to) {

        if (isPhoneNumber(to)) {
            return builder.number(new Number.Builder(to).build());
        }
        return builder.client(new Client.Builder(to).build());
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
                        .recordingStatusCallback("/call/recording")
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
        ResourceSet<com.twilio.rest.api.v2010.account.Call> outgoingCalls =
                com.twilio.rest.api.v2010.account.Call.reader()
                        .setFrom(from)
                        .read(twilioRestClient);

        ResourceSet<com.twilio.rest.api.v2010.account.Call> receivedCalls =
                com.twilio.rest.api.v2010.account.Call.reader().setTo(from).read(twilioRestClient);
        Set<Call> response =
                new TreeSet<>(
                        Comparator.comparing(Call::getDateCreated, Comparator.reverseOrder()));
        for (com.twilio.rest.api.v2010.account.Call call : outgoingCalls) {
            response.add(Call.fromCall(call));
        }
        for (com.twilio.rest.api.v2010.account.Call call : receivedCalls) {
            response.add(Call.fromCall(call));
        }
        return response;
    }

    public void handleVoiceMailRecordings(String recordingUrl, String callSid) {
        Optional<Call> call = Optional.empty();

        Iterator<com.twilio.rest.api.v2010.account.Call> childCallIterator =
                com.twilio.rest.api.v2010.account.Call.reader()
                        .setParentCallSid(callSid)
                        .limit(1)
                        .read(twilioRestClient)
                        .iterator();
        if (childCallIterator.hasNext()) {
            call = Optional.ofNullable(Call.fromCall(childCallIterator.next()));
        }
        call.ifPresent(
                c -> {
                    log.info("Call info {}", c);
                    messageService.sendMessage(
                            c.getFrom(),
                            MessageRequest.builder()
                                    .to(c.getTo())
                                    .body("You have a new voice mail. " + recordingUrl)
                                    .build());
                });
    }
}
