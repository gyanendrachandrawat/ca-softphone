package com.consultadd.controller.twilio;

import com.consultadd.security.UserPrincipal;
import com.consultadd.service.CallService;
import com.twilio.twiml.TwiMLException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/call")
@Slf4j
@RequiredArgsConstructor
public class CallController {

    private final CallService callService;

    @PostMapping(value = "/voice", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> getConnectClientVoice(
            @RequestParam(value = "To", required = false) String to,
            @RequestParam(value = "From", required = false) String from)
            throws TwiMLException {
        log.info("Called from {} to {}", from, to);
        String xml = callService.createVoiceResponse(to, from);
        return new ResponseEntity<>(xml, HttpStatus.OK);
    }

    @PostMapping(value = "/direct-client", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> getDirectClientVoice(
            @RequestParam(value = "To", required = false) String to,
            @RequestParam(value = "From", required = false) String from)
            throws TwiMLException {
        log.info("Called Coming from {} to {}", from, to);
        String xml = callService.getRedirectToClientVoiceResponse(to);
        return new ResponseEntity<>(xml, HttpStatus.OK);
    }

    @PostMapping(value = "/voicemail", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> recordVoiceMail(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(callService.recordVoiceMail());
    }

    @PostMapping(value = "/hangup", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> hangupCall() {
        return ResponseEntity.ok(callService.hangupCall());
    }

    @GetMapping("/logs")
    public ResponseEntity<Object> listAllCalls() {
        return ResponseEntity.ok(callService.listAllCalls(getPrincipal().getTwilioNumber()));
    }

    @PostMapping(value = "/recording")
    public ResponseEntity<Object> handleVoiceMailRecordings(
            @RequestParam(value = "RecordingUrl") String recordingUrl,
            @RequestParam(value = "CallSid") String callSid) {
        callService.handleVoiceMailRecordings(recordingUrl, callSid);
        return ResponseEntity.ok().build();
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
