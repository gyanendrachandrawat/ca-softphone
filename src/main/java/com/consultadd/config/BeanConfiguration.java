package com.consultadd.config;

import com.consultadd.security.UserPrincipal;
import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
    public AuditorAware<String> aware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof AnonymousAuthenticationToken
                    || authentication.getPrincipal() == null) {
                return Optional.empty();
            }
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            return Optional.ofNullable(String.valueOf(principal.getId()));
        };
    }
}
