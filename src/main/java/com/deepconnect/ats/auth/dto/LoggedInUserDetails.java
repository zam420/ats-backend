package com.deepconnect.ats.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.deepconnect.ats.model.User;
import java.util.Collection;
import lombok.Data;

@Data 
public class LoggedInUserDetails implements UserDetails {

    private final Long userId;
    private final String username;
    private final String email;
    private final String password;
    private final boolean locked;
    private final boolean deactivated;//used for newly registered users as their accounts will be deactivated at first.
    private final boolean credentialsExpired;
    private final Collection<? extends GrantedAuthority> authorities;

    // Constructor for database authentication
    public LoggedInUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.userId = user.getId();
        this.username = user.getFullName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = authorities;//this includes roles only
        this.locked = user.getLocked(); 
        this.deactivated = user.getDeactivated(); 
        this.credentialsExpired = user.getCredentialsExpired(); 
    }

    public LoggedInUserDetails(Long userId, String email, 
            boolean deactivated, boolean locked, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = null;
        this.email = email;
        this.password = null;
        this.authorities = authorities;//this includes both roles and permissions`
        this.locked = locked;
        this.deactivated = deactivated;
        this.credentialsExpired = false; //TODO: user.getCredentialsExpired();
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    

}
