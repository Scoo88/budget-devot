package hr.scuric.dewallet.budget.repository;

import hr.scuric.dewallet.budget.models.entity.ExpenseEntity;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long>, JpaSpecificationExecutor<ExpenseEntity> {
    List<ExpenseEntity> findAllByClientId(Long clientId);

    Optional<ExpenseEntity> findByIdAndClientId(Long id, Long clientId);

    List<ExpenseEntity> findAllByCategoryId(Long categoryId);

    List<ExpenseEntity> findAll(@Nullable Specification<ExpenseEntity> specification);

}
