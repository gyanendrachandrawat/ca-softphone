package com.consultadd.util;

import com.consultadd.security.UserPrincipal;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Slf4j
public class AuthenticationUtility {
    private AuthenticationUtility() {}

    public static UserPrincipal getPrincipal(Principal principal) {
        if (!(principal
                instanceof UsernamePasswordAuthenticationToken passwordAuthenticationToken)) {
            throw new BadCredentialsException("Access is denied.");
        }
        return (UserPrincipal) passwordAuthenticationToken.getPrincipal();
    }
}
