package com.consultadd.controller.twilio;

import com.consultadd.model.twilio.Message;
import com.consultadd.model.twilio.MessageRequest;
import com.consultadd.service.MessageService;
import com.consultadd.util.AuthenticationUtility;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> listAllMessages(Principal principal) {
        return ResponseEntity.ok(
                messageService.listAllMessage(
                        AuthenticationUtility.getPrincipal(principal).getTwilioNumber()));
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendSms(
            @RequestBody MessageRequest request, Principal principal) {
        return ResponseEntity.ok(
                messageService.sendMessage(
                        AuthenticationUtility.getPrincipal(principal).getTwilioNumber(), request));
    }
}
