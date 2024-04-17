package hr.scuric.dewallet.budget.repository;

import hr.scuric.dewallet.budget.enums.ExpenseType;
import hr.scuric.dewallet.budget.models.entity.CategoryEntity;
import hr.scuric.dewallet.budget.models.entity.ExpenseEntity;
import hr.scuric.dewallet.client.models.entity.ClientEntity;
import jakarta.persistence.criteria.Join;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
public class ExpenseSpecification {
    public static Specification<ExpenseEntity> filterBy(Long clientId, Long categoryId, BigDecimal minAmount, BigDecimal maxAmount, LocalDate startDate, LocalDate endDate, ExpenseType type) {
        return Specification
                .where(hasClient(clientId))
                .and(hasCategory(categoryId))
                .and(hasAmountGreaterOrEqualThan(minAmount))
                .and(hasAmountLessOrEqualThan(maxAmount))
                .and(hasType(type))
                .and(hasDate(startDate, endDate));
    }

    private static Specification<ExpenseEntity> hasClient(Long clientId) {
        return (root, query, criteriaBuilder) -> {
            Join<ExpenseEntity, ClientEntity> expensesClient = root.join("client");
            return criteriaBuilder.equal(expensesClient.get("id"), clientId);
        };
    }

    private static Specification<ExpenseEntity> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            Join<ExpenseEntity, CategoryEntity> expensesCategory = root.join("category");
            return categoryId == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(expensesCategory.get("id"), categoryId);
        };
    }

    private static Specification<ExpenseEntity> hasAmountLessOrEqualThan(BigDecimal maxAmount) {
        return (root, query, criteriaBuilder) -> maxAmount == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount);
    }

    private static Specification<ExpenseEntity> hasAmountGreaterOrEqualThan(BigDecimal minAmount) {
        return (root, query, criteriaBuilder) -> minAmount == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount);
    }

    private static Specification<ExpenseEntity> hasType(ExpenseType type) {
        return (root, query, criteriaBuilder) -> type == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("type"), type);
    }

    private static Specification<ExpenseEntity> hasDate(LocalDate startDate, LocalDate endDate) {
        String createdAt = "createdAt";
        return (root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null && endDate.plusDays(1).isAfter(startDate)) {
                return criteriaBuilder.between(root.get(createdAt), startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
            } else if (startDate != null && endDate == null && startDate.isBefore(LocalDate.now())) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(createdAt), startDate.atStartOfDay());
            } else if (endDate != null && startDate == null && endDate.isBefore(LocalDate.now())) {
                return criteriaBuilder.lessThan(root.get(createdAt), endDate.plusDays(1).atStartOfDay());
            } else {
                return criteriaBuilder.conjunction();
            }
        };
    }
}
