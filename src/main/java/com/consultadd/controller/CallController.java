package com.consultadd.controller;

import com.consultadd.service.CallService;
import com.twilio.twiml.TwiMLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/call")
@Slf4j
public class CallController {

    @Autowired CallService callService;

    @RequestMapping(
            value = "/voice",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> getConnectClientVoice(
            @RequestParam(value = "To", required = false) String to,
            @RequestParam(value = "From", required = false) String from)
            throws TwiMLException {
        log.info("Called from {} to {}", from, to);
        String xml = callService.createVoiceResponse(to, from);
        return new ResponseEntity<>(xml, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/direct-client",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> getDirectClientVoice(
            @RequestParam(value = "To", required = false) String to,
            @RequestParam(value = "From", required = false) String from)
            throws TwiMLException {
        log.info("Called Coming from {} to {}", from, to);
        String xml = callService.getRedirectToClientVoiceResponse(to);
        return new ResponseEntity<>(xml, HttpStatus.OK);
    }
}
