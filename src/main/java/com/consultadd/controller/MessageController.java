package com.consultadd.controller;

import com.consultadd.model.Message;
import com.consultadd.model.MessageRequest;
import com.consultadd.service.MessageService;
import com.twilio.twiml.TwiMLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/send", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> getConnectClientSMS(
            @RequestParam(value = "From", required = false) String from,
            @RequestParam(value = "To", required = false) String to,
            @RequestParam(value = "Body", required = false) String body)
            throws TwiMLException {
        log.info("Message Sent:: From {} :: To {} :: body {}", from, to, body);
        String xml = messageService.createSMSResponse(to, body, from);
        return new ResponseEntity<>(xml, HttpStatus.OK);
    }

    @PostMapping(value = "/receive", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> getRedirectToClientMessageResponse(
            @RequestParam(value = "From", required = false) String from,
            @RequestParam(value = "To", required = false) String to,
            @RequestParam(value = "Body", required = false) String body)
            throws TwiMLException {
        log.info("Message Received :: From {} :: To {} :: body {}", from, to, body);

        String xml = messageService.getRedirectToClientMessageResponse(from, to, body);
        return new ResponseEntity<>(xml, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> listAllMessages(
            @RequestParam(value = "from", required = false) String from) {
        return ResponseEntity.ok(messageService.listAllMessage(from));
    }

    @PostMapping("/sendsms")
    public ResponseEntity<Message> sendSms(@RequestBody MessageRequest request) {
        return ResponseEntity.ok(messageService.sendMessage(request));
    }
}
