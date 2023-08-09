package com.consultadd.controller.twilio;

import com.consultadd.model.twilio.DeviceInfo;
import com.consultadd.service.UserInfoService;
import com.consultadd.util.AuthenticationUtility;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
@Slf4j
public class DeviceController {

    private final UserInfoService userService;

    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @PostMapping(value = "/token")
    public ResponseEntity<DeviceInfo> getUserTokens(Principal principal) {
        DeviceInfo user =
                userService.createToken(
                        AuthenticationUtility.getPrincipal(principal).getClientId().toString());
        return ResponseEntity.ok(user);
    }
}
