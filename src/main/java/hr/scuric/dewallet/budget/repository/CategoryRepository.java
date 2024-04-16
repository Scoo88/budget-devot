package hr.scuric.dewallet.budget.repository;

import hr.scuric.dewallet.budget.models.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByIdAndClientId(Long id, Long clientId);

    List<CategoryEntity> findAllByClientId(Long clientId);
}
