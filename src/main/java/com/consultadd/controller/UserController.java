package com.consultadd.controller;

import com.consultadd.model.UserInfo;
import com.consultadd.security.UserPrincipal;
import com.consultadd.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/device")
@Slf4j
public class UserController {

    @Autowired private UserInfoService userService;

    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @PostMapping(value = "/token")
    public ResponseEntity<UserInfo> getUserTokens() {
        UserInfo user = userService.createToken(getPrincipal().getClientId().toString());
        return ResponseEntity.ok(user);
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
