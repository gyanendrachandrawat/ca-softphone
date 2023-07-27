package com.consultadd.controller.twilio;

import com.consultadd.model.twilio.Message;
import com.consultadd.model.twilio.MessageRequest;
import com.consultadd.security.UserPrincipal;
import com.consultadd.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class MessageController {

    @Autowired private MessageService messageService;

    @GetMapping("/list")
    public ResponseEntity<Object> listAllMessages() {
        return ResponseEntity.ok(messageService.listAllMessage(getPrincipal().getTwilioNumber()));
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendSms(@RequestBody MessageRequest request) {
        System.out.println(getPrincipal().getTwilioNumber());
        System.out.println(request.toString());
        return ResponseEntity.ok(
                messageService.sendMessage(getPrincipal().getTwilioNumber(), request));
    }

    private UserPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken
                || authentication.getPrincipal() == null) {
            throw new BadCredentialsException("Access is denied.");
        }
        return (UserPrincipal) authentication.getPrincipal();
    }
}
