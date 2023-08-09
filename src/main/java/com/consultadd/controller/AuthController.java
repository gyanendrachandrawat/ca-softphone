package com.consultadd.controller;

import com.consultadd.model.RefreshToken;
import com.consultadd.model.dto.AuthRequest;
import com.consultadd.model.dto.AuthResponse;
import com.consultadd.model.dto.ChangePasswordRequest;
import com.consultadd.model.dto.ChangePasswordRequest.PasswordEvent;
import com.consultadd.security.JwtTokenProvider;
import com.consultadd.security.UserPrincipal;
import com.consultadd.service.RefreshTokenService;
import com.consultadd.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    @PostMapping("/login")
    public AuthResponse getToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authRequest.userName(), authRequest.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String refreshToken = refreshTokenService.generateRefreshToken(principal.getId());

        AuthResponse jwt = tokenProvider.generateTokenFromAuthentication(authentication);
        jwt.setRefreshToken(refreshToken);
        return jwt;
    }

    @PostMapping("/refresh")
    public AuthResponse getRefreshToken(@RequestParam String refreshTokenId) {

        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenId);

        AuthResponse authResponse =
                tokenProvider.generateTokenFromUserId(refreshToken.getUser().getId());
        String newRefreshToken =
                refreshTokenService.invalidateAndGenerateRefreshToken(
                        refreshToken.getUser().getId(), refreshTokenId);

        authResponse.setRefreshToken(newRefreshToken);
        return authResponse;
    }

    public ResponseEntity changePassword(ChangePasswordRequest passwordInput) {
        if (passwordInput.getEvent().equals(PasswordEvent.RESET_PASSWORD))
            return userService.resetPassword(passwordInput);
        else return userService.updatePassword(passwordInput);
    }
}
