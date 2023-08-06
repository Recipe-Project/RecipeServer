package com.recipe.app.src.user.application;

import com.recipe.app.src.user.application.port.UserRepository;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.exception.NotFoundUserException;
import com.recipe.app.src.user.infra.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new SecurityUser(userRepository.findById(Long.parseLong(username))
                .orElseThrow(NotFoundUserException::new));
    }
}
