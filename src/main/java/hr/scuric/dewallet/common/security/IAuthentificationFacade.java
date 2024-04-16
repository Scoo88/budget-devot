package hr.scuric.dewallet.common.security;

import hr.scuric.dewallet.client.models.entity.ClientEntity;
import org.springframework.security.core.Authentication;

public interface IAuthentificationFacade {
    Authentication getAuthentication();

    Long getPrincipalId();

    ClientEntity getClientEntity();
}
