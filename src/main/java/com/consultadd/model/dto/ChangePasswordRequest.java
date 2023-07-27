package com.consultadd.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordRequest {
    public enum PasswordEvent {
        RESET_PASSWORD,

        UPDATE_PASSWORD
    }

    private String email;

    private PasswordEvent event;

    private String oldPassword;

    private String newPassword;
}
