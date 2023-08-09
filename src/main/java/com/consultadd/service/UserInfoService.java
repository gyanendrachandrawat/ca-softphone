package com.consultadd.service;

import com.consultadd.model.twilio.DeviceInfo;
import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.ChatGrant;
import com.twilio.jwt.accesstoken.VoiceGrant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    @Value("${twilio.account_sid}")
    private String acctSid;

    @Value("${twilio.twiml.app.sid}")
    private String applicationSid;

    @Value("${twilio.chat.sid}")
    private String chatSid;

    @Value("${twilio.api.key}")
    private String apiKey;

    @Value("${twilio.api.secret}")
    private String apiSecret;

    public DeviceInfo createToken(String identity) {

        return createJsonAccessToken(identity);
    }

    private DeviceInfo createJsonAccessToken(String identity) {

        VoiceGrant grant = new VoiceGrant();
        grant.setOutgoingApplicationSid(applicationSid);
        grant.setIncomingAllow(true);

        ChatGrant chatGrant = new ChatGrant();
        chatGrant.setServiceSid(chatSid);

        AccessToken accessToken =
                new AccessToken.Builder(acctSid, apiKey, apiSecret)
                        .grants(List.of(grant, chatGrant))
                        .identity(identity)
                        .build();

        String token = accessToken.toJwt();

        return new DeviceInfo(identity, token);
    }
}
