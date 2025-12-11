package com.marin.dulja.personalfinancetrackerbe.security;

import com.marin.dulja.personalfinancetrackerbe.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final UUID userId;
    private final String email;
    private final String password;
    private final boolean enabled;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.enabled = user.isEnabled();
        this.authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

