package com.consultadd.service;

import com.consultadd.exceptions.ApplicationException;
import com.consultadd.exceptions.ValidationException;
import com.consultadd.model.RefreshToken;
import com.consultadd.model.User;
import com.consultadd.repository.RefreshTokenRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value(value = "${jwt.refreshExpirationDateInMs}")
    Long refreshTokenDurationMs;

    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    public String generateRefreshToken(Long userId) {
        User user = userService.findById(userId);
        RefreshToken refreshToken =
                RefreshToken.builder()
                        .id(UUID.randomUUID().toString())
                        .user(user)
                        .expirationAt(Instant.now().plusMillis(refreshTokenDurationMs))
                        .isValid(true)
                        .build();
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getId();
    }

    public String invalidateAndGenerateRefreshToken(Long userId, String refreshTokenId) {
        RefreshToken refreshToken =
                refreshTokenRepository
                        .findById(refreshTokenId)
                        .orElseThrow(() -> new ValidationException("Invalid refresh token."));
        refreshToken.setIsValid(false);
        refreshTokenRepository.save(refreshToken);
        return generateRefreshToken(userId);
    }

    public RefreshToken findByToken(String token) {
        RefreshToken refreshToken =
                refreshTokenRepository
                        .findById(token)
                        .orElseThrow(
                                () ->
                                        new ApplicationException(
                                                "Refresh Token is invalid or expired. Please login"
                                                        + " again"));
        verifyRefreshToken(refreshToken);
        return refreshToken;
    }

    private void verifyRefreshToken(RefreshToken refreshToken) {
        if (Boolean.FALSE.equals(refreshToken.getIsValid())) {
            invalidatePreviousTokens(refreshToken.getUser().getId());
            throw new ApplicationException("Refresh Token is invalid. Please login again");
        }
        if (refreshToken.getExpirationAt().isBefore(Instant.now())) {
            throw new ApplicationException("Refresh Token is expired. Please login again");
        }
    }

    private void invalidatePreviousTokens(Long userId) {
        List<RefreshToken> invalidTokens =
                refreshTokenRepository.findByUser_IdEqualsAndIsValidIsTrue(userId).stream()
                        .filter(RefreshToken::getIsValid)
                        .collect(Collectors.toUnmodifiableList());

        invalidTokens.forEach(tk -> tk.setIsValid(false));
        refreshTokenRepository.saveAll(invalidTokens);
    }
}
