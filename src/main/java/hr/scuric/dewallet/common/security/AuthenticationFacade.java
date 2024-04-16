package hr.scuric.dewallet.common.security;

import hr.scuric.dewallet.client.models.entity.ClientEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade implements IAuthentificationFacade {
    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public Long getPrincipalId() {
        CustomUserDetails principal = (CustomUserDetails) this.getAuthentication().getPrincipal();
        return principal.getId();
    }

    @Override
    public ClientEntity getClientEntity() {
        CustomUserDetails principal = (CustomUserDetails) this.getAuthentication().getPrincipal();
        return principal.getClientEntity();
    }
}
