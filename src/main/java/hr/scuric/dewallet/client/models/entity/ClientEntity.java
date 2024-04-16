package hr.scuric.dewallet.client.models.entity;

import hr.scuric.dewallet.budget.models.entity.CategoryEntity;
import hr.scuric.dewallet.client.models.request.ClientRequest;
import hr.scuric.dewallet.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@Table(name = "clients")
public class ClientEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clients_id_seq_generator")
    @SequenceGenerator(name = "clients_id_seq_generator", sequenceName = "clients_id_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "balance")
    private BigDecimal balance;

    @OneToMany(mappedBy = "client")
    private Set<CategoryEntity> categories;

    public static ClientEntity fromRequest(ClientRequest request, String password) {
        ClientEntity entity = new ClientEntity();
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setDateOfBirth(request.getDateOfBirth());
        entity.setPhone(request.getPhone());
        entity.setEmail(request.getEmail());
        entity.setPassword(password);
        entity.setBalance(BigDecimal.valueOf(2000L));
        return entity;
    }
}
