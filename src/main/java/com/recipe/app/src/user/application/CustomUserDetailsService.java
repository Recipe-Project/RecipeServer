package com.recipe.app.src.user.application;

import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {

        this.userService = userService;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userService.findByUserId(Long.parseLong(username));

        if (user.isDeleted()) {
            throw new UsernameNotFoundException("탈퇴한 사용자입니다.");
        }

        return new SecurityUser(user);
    }
}
