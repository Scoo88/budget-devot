package hr.scuric.dewallet.budget.service;

import hr.scuric.dewallet.budget.models.entity.CategoryEntity;
import hr.scuric.dewallet.budget.models.request.CategoryRequest;
import hr.scuric.dewallet.budget.models.response.CategoryResponse;
import hr.scuric.dewallet.budget.repository.CategoryRepository;
import hr.scuric.dewallet.client.models.entity.ClientEntity;
import hr.scuric.dewallet.common.exceptions.DeWalletException;
import hr.scuric.dewallet.common.security.IAuthentificationFacade;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CategoryServiceTest {
    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private IAuthentificationFacade authentication;
    @Mock
    private PasswordEncoder passwordEncoder;

    private CategoryRequest request;
    private CategoryEntity entity;
    private CategoryResponse categoryResponse;
    private Long id;
    private ClientEntity clientEntity;

    @BeforeEach
    void setUp() {
        this.id = 1L;
        this.request = new CategoryRequest();
        this.request.setName("test");

        this.clientEntity = new ClientEntity();
        this.clientEntity.setId(this.id);
        this.clientEntity.setFirstName("First");
        this.clientEntity.setLastName("Last");
        this.clientEntity.setDateOfBirth(LocalDate.of(1988, 11, 22));
        this.clientEntity.setPhone("003859112345678");
        this.clientEntity.setEmail("test@email.com");
        this.clientEntity.setPassword(this.passwordEncoder.encode("test"));
        this.clientEntity.setBalance(new BigDecimal("2000"));

        this.entity = new CategoryEntity();
        this.entity.setId(this.id);
        this.entity.setName("test");
        this.entity.setClient(this.clientEntity);

        this.categoryResponse = new CategoryResponse();
        this.categoryResponse.setId(this.id);
        this.categoryResponse.setName("test");
    }

    @Test
    void insertCategory_UserRegistration() {
        CategoryResponse response = this.categoryService.insertCategory(this.request, Optional.ofNullable(this.clientEntity));
        assertEquals(this.categoryResponse.getName(), response.getName());
    }

    @Test
    void insertCategory() {
        CategoryResponse response = this.categoryService.insertCategory(this.request, Optional.empty());
        assertEquals(this.categoryResponse.getName(), response.getName());
    }

    @Test
    void getCategory() {
        try {
            when(this.authentication.getPrincipalId()).thenReturn(this.id);
            when(this.categoryRepository.findByIdAndClientId(this.id, this.id)).thenReturn(Optional.ofNullable(this.entity));
            CategoryResponse response = this.categoryService.getCategory(this.id);
            assertEquals(this.categoryResponse.getName(), response.getName());
        } catch (DeWalletException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    void getCategory_EntityNotFound() {
        when(this.authentication.getPrincipalId()).thenReturn(this.id);
        when(this.categoryRepository.findByIdAndClientId(this.id, this.id)).thenReturn(Optional.empty());

        assertThrows(DeWalletException.class, () -> this.categoryService.getCategory(this.id));
    }

    @Test
    void getCategories() {
        when(this.authentication.getPrincipalId()).thenReturn(this.id);
        when(this.categoryRepository.findAllByClientId(this.id)).thenReturn(Collections.singletonList(this.entity));

        List<CategoryResponse> response = this.categoryService.getCategories();
        assertEquals(response.get(0).getName(), this.categoryResponse.getName());
    }

    @Test
    void updateCategory() {
        try {
            when(this.authentication.getPrincipalId()).thenReturn(this.id);
            when(this.categoryRepository.findByIdAndClientId(this.id, this.id)).thenReturn(Optional.of(this.entity));

            this.request.setName("updateTest");

            CategoryResponse response = this.categoryService.updateCategory(this.id, this.request);
            assertEquals(response.getName(), this.request.getName());
        } catch (DeWalletException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    void updateCategoryStatus() {
        try {
            when(this.authentication.getPrincipalId()).thenReturn(this.id);
            when(this.categoryRepository.findByIdAndClientId(this.id, this.id)).thenReturn(Optional.of(this.entity));

            CategoryResponse response = this.categoryService.updateCategoryStatus(this.id, false);
            assertEquals(false, response.getIsActive());
        } catch (DeWalletException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    void deleteCategory() {
        try {
            when(this.authentication.getPrincipalId()).thenReturn(this.id);
            when(this.categoryRepository.findByIdAndClientId(this.id, this.id)).thenReturn(Optional.of(this.entity));

            HttpStatus httpStatus = this.categoryService.deleteCategory(this.id);
            assertEquals(HttpStatus.NO_CONTENT, httpStatus);
        } catch (DeWalletException e) {
            log.error(e.getMessage());
        }
    }
}