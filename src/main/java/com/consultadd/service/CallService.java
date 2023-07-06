package com.consultadd.service;

import com.consultadd.Identity;
import com.consultadd.util.Constants;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Client;
import com.twilio.twiml.voice.Dial;
import com.twilio.twiml.voice.Number;
import com.twilio.twiml.voice.Record;
import com.twilio.twiml.voice.Say;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CallService {
    @Autowired private Identity identities;

    public String createVoiceResponse(String to, String from) {
        VoiceResponse voiceTwimlResponse;

        if (to != null) {

            Dial.Builder dialBuilder =
                    new Dial.Builder().callerId(identities.getPhoneById(from.substring(7)));

            Dial.Builder dialBuilderWithReceiver = addChildReceiver(dialBuilder, to);

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
        Record record = new Record.Builder().timeout(30).transcribe(true).build();

        Dial dial = new Dial.Builder().client(client).build();

        voiceTwimlResponse = new VoiceResponse.Builder().say(say).dial(dial).record(record).build();
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
}
