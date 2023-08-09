package com.consultadd.security;

import com.consultadd.model.User;
import com.consultadd.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String empId) throws UsernameNotFoundException {
        User user =
                repo.findById(Long.valueOf(empId))
                        .orElseThrow(
                                () ->
                                        new UsernameNotFoundException(
                                                "The email provided is not registered. Please check"
                                                        + " the email address and try again."));
        return UserPrincipal.create(user);
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        Optional<User> user = repo.findById(id);

        return UserPrincipal.create(
                user.orElseThrow(
                        () -> new UsernameNotFoundException("User with id " + id + "not present")));
    }
}
