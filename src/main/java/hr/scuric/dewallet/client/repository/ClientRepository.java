package hr.scuric.dewallet.client.repository;

import hr.scuric.dewallet.client.models.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    ClientEntity findByEmail(String email);
}
