package com.consultadd;

import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Value("${twilio.account_sid}")
    public String ACCOUNT_SID;

    @Value("${twilio.auth_token}")
    public String AUTH_TOKEN;

    @Bean
    public TwilioRestClient getTwilioClient() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        return Twilio.getRestClient();
    }

    @Bean
    public Identity getIdentityBean() {
        return Identity.getInstance();
    }
}
