package hr.scuric.dewallet.budget.service;

import hr.scuric.dewallet.budget.models.entity.CategoryEntity;
import hr.scuric.dewallet.budget.models.request.CategoryRequest;
import hr.scuric.dewallet.budget.models.response.CategoryResponse;
import hr.scuric.dewallet.budget.repository.CategoryRepository;
import hr.scuric.dewallet.client.models.entity.ClientEntity;
import hr.scuric.dewallet.common.exceptions.DeWalletException;
import hr.scuric.dewallet.common.exceptions.Messages;
import hr.scuric.dewallet.common.security.IAuthentificationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final IAuthentificationFacade authentication;

    public CategoryResponse insertCategory(CategoryRequest request, Optional<ClientEntity> optionalClientEntity) {
        CategoryEntity entity;
        if (optionalClientEntity.isEmpty()) {
            entity = CategoryEntity.fromRequest(this.authentication.getClientEntity(), request);
        } else {
            entity = CategoryEntity.fromRequest(optionalClientEntity.get(), request);
        }
        this.categoryRepository.saveAndFlush(entity);
        return CategoryResponse.fromEntity(entity);
    }

    public List<CategoryResponse> getCategories() {
        List<CategoryEntity> all = this.categoryRepository.findAllByClientId(this.authentication.getPrincipalId());
        List<CategoryResponse> response = new ArrayList<>();
        all.forEach(entity -> {
            response.add(CategoryResponse.fromEntity(entity));
        });
        return response;
    }

    public CategoryResponse getCategory(Long id) throws DeWalletException {
        return CategoryResponse.fromEntity(this.getById(id));
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) throws DeWalletException {
        CategoryEntity entity = this.getById(id);
        if (Objects.nonNull(request.getName())) {
            entity.setName(request.getName());
        }
        this.categoryRepository.saveAndFlush(entity);
        return CategoryResponse.fromEntity(entity);
    }

    public CategoryResponse updateCategoryStatus(Long id, boolean active) throws DeWalletException {
        CategoryEntity entity = this.getById(id);
        entity.setIsActive(active);
        this.categoryRepository.saveAndFlush(entity);
        return CategoryResponse.fromEntity(entity);
    }

    public HttpStatus deleteCategory(Long id) throws DeWalletException {
        CategoryEntity entity = this.getById(id);
        this.categoryRepository.delete(entity);
        return HttpStatus.NO_CONTENT;
    }

    public CategoryEntity getById(Long id) throws DeWalletException {
        Optional<CategoryEntity> optionalCategory = this.categoryRepository.findByIdAndClientId(id, this.authentication.getPrincipalId());
        if (optionalCategory.isEmpty()) {
            throw new DeWalletException(Messages.ENTITY_NOT_FOUND);
        }
        return optionalCategory.get();
    }
}
