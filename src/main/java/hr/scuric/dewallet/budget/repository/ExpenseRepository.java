package hr.scuric.dewallet.budget.repository;

import hr.scuric.dewallet.budget.models.entity.ExpenseEntity;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long>, JpaSpecificationExecutor<ExpenseEntity> {
    List<ExpenseEntity> findAllByClientId(Long clientId);

    Optional<ExpenseEntity> findByIdAndClientId(Long id, Long clientId);

    @Override
    List<ExpenseEntity> findAll(@Nullable Specification<ExpenseEntity> specification);

    @Query(value = """
            select e.client_id, e.type, sum(e.amount) as total from expenses e
            where e.client_id = :clientId
            and e.is_active = true
            and e.created_at >= :start
            and e.created_at < :end
            group by e.client_id, e.type
            """, nativeQuery = true)
    List<ExpenseStatisticsView> getStatistics(@Param(value = "clientId") Long clientId, @Param(value = "start") LocalDateTime start, @Param(value = "end") LocalDateTime end);

    @Query(value = """
            select TO_CHAR(e.created_at, 'MM-YYYY') as "month", e."type", sum(e.amount) as total from expenses e
            where e.client_id = :clientId
            and e.is_active = true
            and e.created_at >= :start
            and e.created_at <= :end
            group by e.client_id, e."type", "month"
            order by "month" desc
            """, nativeQuery = true)
    List<ExpensesPerMonth> getOverview(@Param(value = "clientId") Long clientId, @Param(value = "start") LocalDateTime start, @Param(value = "end") LocalDateTime end);
}
