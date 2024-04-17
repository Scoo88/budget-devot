package hr.scuric.dewallet.budget.models.entity;

import hr.scuric.dewallet.budget.models.request.CategoryRequest;
import hr.scuric.dewallet.client.models.entity.ClientEntity;
import hr.scuric.dewallet.common.models.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

@Data
@Entity
@Table(name = "categories", uniqueConstraints = {@UniqueConstraint(name = "name_client_unique", columnNames = {
        "name", "client_id"})})
@EqualsAndHashCode(callSuper = true)
public class CategoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_id_seq_generator")
    @SequenceGenerator(name = "categories_id_seq_generator", sequenceName = "categories_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "client_id", updatable = false, nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ClientEntity client;

    @OneToMany(mappedBy = "category")
    private Set<ExpenseEntity> expenses;

    public static CategoryEntity fromRequest(ClientEntity clientEntity, CategoryRequest request) {
        CategoryEntity entity = new CategoryEntity();
        entity.setName(request.getName());
        entity.setClient(clientEntity);
        return entity;
    }
}
