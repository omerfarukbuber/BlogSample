package com.omerfbuber.service.user;

import com.omerfbuber.entity.User;
import com.omerfbuber.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailWithRoleAndPermissions(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public boolean containsPermission(CustomUserDetails customUserDetails, String permission) {
        var authorities = customUserDetails.getAuthorities();
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(permission));
    }

    public User getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }
}
