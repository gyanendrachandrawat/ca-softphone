package com.consultadd.controller;

import com.consultadd.model.twilio.Message;
import com.consultadd.model.twilio.MessageRequest;
import com.consultadd.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
@Slf4j
public class MessageController {

    @Autowired private MessageService messageService;

    @GetMapping("/list")
    public ResponseEntity<Object> listAllMessages(
            @RequestParam(value = "from", required = false) String from) {
        return ResponseEntity.ok(messageService.listAllMessage(from));
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendSms(@RequestBody MessageRequest request) {
        return ResponseEntity.ok(messageService.sendMessage(request));
    }
}
