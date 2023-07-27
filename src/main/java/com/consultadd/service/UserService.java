package com.consultadd.service;

import com.consultadd.exceptions.ApplicationException;
import com.consultadd.exceptions.ValidationException;
import com.consultadd.model.User;
import com.consultadd.model.dto.ChangePasswordRequest;
import com.consultadd.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new ApplicationException("User not found for Id " + userId));
    }

    public ResponseEntity resetPassword(ChangePasswordRequest input) {
        User user =
                userRepository
                        .findByEmailIgnoreCase(input.getEmail())
                        .orElseThrow(
                                () ->
                                        new ApplicationException(
                                                "The email provided is not registered. Please check"
                                                        + " the email address and try again."));

        user.setPassword(passwordEncoder.encode(input.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password Reset Successful.");
    }

    public ResponseEntity updatePassword(ChangePasswordRequest input) {
        User user =
                userRepository
                        .findByEmailIgnoreCase(input.getEmail())
                        .orElseThrow(
                                () ->
                                        new ApplicationException(
                                                "The email provided is not registered. Please check"
                                                        + " the email address and try again."));

        if (!passwordEncoder.matches(input.getOldPassword(), user.getPassword())) {
            throw new ValidationException(
                    "Password reset failed. The current password doesn't match the old password.");
        }
        if (passwordEncoder.matches(input.getNewPassword(), user.getPassword())) {
            throw new ApplicationException("New Password should not be same as Old Password!");
        }
        user.setPassword(passwordEncoder.encode(input.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password Updated Successful.");
    }

    public User findByClientId(String clientId) {
        UUID clientUUID = UUID.fromString(clientId);
        return userRepository
                .findByClientId(clientUUID)
                .orElseThrow(() -> new ApplicationException("Invalid Client Id."));
    }

    public User findByTwilioNumber(String twilioNumber) {
        return userRepository
                .findByTwilioNumber(twilioNumber)
                .orElseThrow(() -> new ApplicationException("Invalid twilio number."));
    }
}
