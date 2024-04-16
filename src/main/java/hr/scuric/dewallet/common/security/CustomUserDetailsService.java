package hr.scuric.dewallet.common.security;

import hr.scuric.dewallet.client.models.entity.ClientEntity;
import hr.scuric.dewallet.client.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Objects;

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ClientEntity entity = clientRepository.findByEmail(email);
        if (Objects.isNull(entity)) {
            throw new UsernameNotFoundException("Client not found.");
        }
        return new CustomUserDetails(entity);
    }
}
