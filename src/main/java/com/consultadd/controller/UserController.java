package com.consultadd.controller;

import com.consultadd.model.UserInfo;
import com.consultadd.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
@Slf4j
public class UserController {

    @Autowired private UserInfoService userService;

    // to get the usertoken
    @RequestMapping(value = "token", method = RequestMethod.GET)
    public ResponseEntity<UserInfo> getUserTokens(@RequestParam("phoneNumber") String phoneNumber) {
        log.info("called token {}", phoneNumber);
        UserInfo user = userService.createToken(phoneNumber);
        return ResponseEntity.ok(user);
    }
}
