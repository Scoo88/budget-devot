package hr.scuric.dewallet.common.security;

import hr.scuric.dewallet.client.models.entity.ClientEntity;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private ClientEntity clientEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return clientEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return clientEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return clientEntity.getIsActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return clientEntity.getIsActive();
    }

    public Long getId() {
        return clientEntity.getId();
    }

    public ClientEntity getClientEntity() {
        return clientEntity;
    }
}
