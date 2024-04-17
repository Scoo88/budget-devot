package hr.scuric.dewallet.budget.models.entity;

import hr.scuric.dewallet.budget.enums.ExpenseType;
import hr.scuric.dewallet.budget.models.request.ExpenseRequest;
import hr.scuric.dewallet.client.models.entity.ClientEntity;
import hr.scuric.dewallet.common.models.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@Entity
@Table(name = "expenses")
@EqualsAndHashCode(callSuper = true)
public class ExpenseEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expenses_id_seq_generator")
    @SequenceGenerator(name = "expenses_id_seq_generator", sequenceName = "expenses_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "amount", nullable = false, scale = 2)
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ExpenseType type;

    @ManyToOne
    @JoinColumn(name = "client_id", updatable = false, nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private CategoryEntity category;

    public static ExpenseEntity fromRequest(ExpenseRequest request, CategoryEntity categoryEntity, ClientEntity clientEntity) {
        ExpenseEntity entity = new ExpenseEntity();
        entity.setType(request.getType());
        entity.setAmount(request.getAmount());
        entity.setDescription(request.getDescription());
        if (Objects.nonNull(categoryEntity)) {
            entity.setCategory(categoryEntity);
        }
        entity.setClient(clientEntity);
        return entity;
    }
}
