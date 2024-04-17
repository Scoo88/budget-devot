package hr.scuric.dewallet.budget.service;

import hr.scuric.dewallet.budget.enums.ExpenseType;
import hr.scuric.dewallet.budget.models.entity.CategoryEntity;
import hr.scuric.dewallet.budget.models.entity.ExpenseEntity;
import hr.scuric.dewallet.budget.models.request.ExpenseRequest;
import hr.scuric.dewallet.budget.models.response.CategoryResponse;
import hr.scuric.dewallet.budget.models.response.ExpenseResponse;
import hr.scuric.dewallet.budget.repository.ExpenseRepository;
import hr.scuric.dewallet.client.models.entity.ClientEntity;
import hr.scuric.dewallet.client.service.ClientService;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@Slf4j
class ExpenseServiceTest {
    @InjectMocks
    private ExpenseService expenseService;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private ClientService clientService;
    @Mock
    private IAuthentificationFacade authentication;
    @Mock
    private PasswordEncoder passwordEncoder;

    private ExpenseRequest expenseRequest;
    private ExpenseEntity entity;
    private ExpenseResponse expenseResponse;
    private Long id;
    private ClientEntity clientEntity;
    private CategoryEntity categoryEntity;
    private CategoryResponse categoryResponse;


    @BeforeEach
    void setUp() {
        this.id = 1L;

        this.clientEntity = new ClientEntity();
        this.clientEntity.setId(this.id);
        this.clientEntity.setFirstName("First");
        this.clientEntity.setLastName("Last");
        this.clientEntity.setDateOfBirth(LocalDate.of(1988, 11, 22));
        this.clientEntity.setPhone("003859112345678");
        this.clientEntity.setEmail("test@email.com");
        this.clientEntity.setPassword(this.passwordEncoder.encode("test"));
        this.clientEntity.setBalance(new BigDecimal("2000"));

        this.expenseRequest = new ExpenseRequest();
        this.expenseRequest.setCategoryId(this.id);
        this.expenseRequest.setType(ExpenseType.INPUT);
        this.expenseRequest.setDescription("test");
        this.expenseRequest.setAmount(BigDecimal.TEN);

        this.categoryResponse = new CategoryResponse();
        this.categoryResponse.setId(this.id);
        this.categoryResponse.setName("test");

        this.categoryEntity = new CategoryEntity();
        this.categoryEntity.setId(this.id);
        this.categoryEntity.setName("test");
        this.categoryEntity.setClient(this.clientEntity);

        this.entity = new ExpenseEntity();
        this.entity.setId(this.id);
        this.entity.setAmount(BigDecimal.TEN);
        this.entity.setDescription("test");
        this.entity.setType(ExpenseType.INPUT);
        this.entity.setClient(this.clientEntity);
        this.entity.setCategory(this.categoryEntity);
        this.entity.setCreatedAt(LocalDateTime.from(LocalDate.of(2024, 4, 15).atStartOfDay()));

        this.expenseResponse = new ExpenseResponse();
        this.expenseResponse.setId(this.id);
        this.expenseResponse.setType(ExpenseType.INPUT);
        this.expenseResponse.setAmount(BigDecimal.TEN);
        this.expenseResponse.setDescription("test");
        this.expenseResponse.setCategory(this.categoryResponse);
    }

    @Test
    void insertExpense() {
        try {
            when(this.categoryService.getById(this.id)).thenReturn(this.categoryEntity);
            ExpenseResponse response = this.expenseService.insertExpense(this.expenseRequest);
            assertEquals(response.getAmount(), this.expenseResponse.getAmount());
        } catch (DeWalletException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    void getExpenses() {
        List<ExpenseEntity> expensesList = this.getExpensesList();

        when(this.authentication.getPrincipalId()).thenReturn(this.id);
        when(this.expenseRepository.findAllByClientId(this.id)).thenReturn(expensesList);

        List<ExpenseResponse> response = this.expenseService.getExpenses();
        assertEquals(response.get(0).getAmount(), expensesList.get(0).getAmount());
    }

    @Test
    void getExpense() {
        try {
            when(this.authentication.getPrincipalId()).thenReturn(this.id);
            when(this.expenseRepository.findByIdAndClientId(this.id, this.id)).thenReturn(Optional.ofNullable(this.entity));
            ExpenseResponse response = this.expenseService.getExpense(this.id);
            assertEquals(response.getAmount(), this.expenseResponse.getAmount());
        } catch (DeWalletException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    void getExpense_ExpenseNotFound() {
        when(this.authentication.getPrincipalId()).thenReturn(this.id);
        when(this.expenseRepository.findByIdAndClientId(this.id, this.id)).thenReturn(Optional.empty());

        assertThrows(DeWalletException.class, () -> this.expenseService.getExpense(this.id));
    }

    @Test
    void updateExpense() {
        try {
            when(this.authentication.getPrincipalId()).thenReturn(this.id);
            when(this.expenseRepository.findByIdAndClientId(this.id, this.id)).thenReturn(Optional.ofNullable(this.entity));
            when(this.categoryService.getById(this.id)).thenReturn(this.categoryEntity);

            this.expenseRequest.setAmount(BigDecimal.valueOf(200L));

            ExpenseResponse response = this.expenseService.updateExpense(this.id, this.expenseRequest);
            assertEquals(response.getAmount(), this.expenseRequest.getAmount());
        } catch (DeWalletException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    void updateExpenseStatus() {
        try {
            when(this.authentication.getPrincipalId()).thenReturn(this.id);
            when(this.expenseRepository.findByIdAndClientId(this.id, this.id)).thenReturn(Optional.ofNullable(this.entity));

            ExpenseResponse response = this.expenseService.updateExpenseStatus(this.id, false);
            assertEquals(false, response.getIsActive());
        } catch (DeWalletException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    void deleteExpense() {
        try {
            when(this.authentication.getPrincipalId()).thenReturn(this.id);
            when(this.expenseRepository.findByIdAndClientId(this.id, this.id)).thenReturn(Optional.ofNullable(this.entity));

            HttpStatus response = this.expenseService.deleteExpense(this.id);
            assertEquals(HttpStatus.NO_CONTENT, response);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

//    @Test
//    void filterExpenses() {
//        List<ExpenseEntity> expensesList = this.getExpensesList();
//        when(this.authentication.getPrincipalId()).thenReturn(this.id);
//        when(this.expenseRepository.findAll()).thenReturn(expensesList);
//        List<ExpenseResponse> response = this.expenseService.filterExpenses(null, null, null, null, null, ExpenseType.INPUT);
//        assertEquals(this.entity.getType(), response.get(0).getType());
//    }
//
//    @Test
//    void getStatistics() {
//        List<ExpenseStatisticsView> statistics = new ArrayList<>();
//        ExpenseStatisticsView esv1 = mock(ExpenseStatisticsView.class);
//        ExpenseStatisticsView esv2 = mock(ExpenseStatisticsView.class);
//        statistics.add(esv1);
//        statistics.add(esv2);
//
//        List<ExpensesPerMonth> expensesPerMonths = new ArrayList<>();
//        ExpensesPerMonth epm1 = mock(ExpensesPerMonth.class);
//        ExpensesPerMonth epm2 = mock(ExpensesPerMonth.class);
//        expensesPerMonths.add(epm1);
//        expensesPerMonths.add(epm2);
//
//        try {
//            when(this.authentication.getPrincipalId()).thenReturn(this.id);
//            when(this.categoryService.getById(this.id)).thenReturn(this.categoryEntity);
//            when(this.expenseRepository.getStatistics(any(), any())).thenReturn(statistics);
//            when(this.expenseRepository.getOverview(any(), any())).thenReturn(expensesPerMonths);
//            ExpenseStatistics response = this.expenseService.getStatistics(Period.Q2, null, 2024, null, null, null);
//            log.error(response.toString());
//        } catch (DeWalletException e) {
//            log.error(e.getMessage());
//        }
//    }

    private List<ExpenseEntity> getExpensesList() {
        List<ExpenseEntity> response = new ArrayList<>();

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(this.id);
        categoryEntity.setName("test");
        categoryEntity.setClient(this.clientEntity);

        for (int i = 0; i < 11; i++) {
            ExpenseEntity expenseEntity = new ExpenseEntity();
            expenseEntity.setId(this.id + i);
            expenseEntity.setAmount(BigDecimal.valueOf(100));
            expenseEntity.setDescription("test");

            if (i % 2 == 0) {
                expenseEntity.setType(ExpenseType.INPUT);
            } else {
                expenseEntity.setType(ExpenseType.OUTPUT);
            }

            if (i == 4) {
                expenseEntity.setIsActive(false);
            }
            if (i == 5) {
                expenseEntity.setIsActive(false);
            }
            expenseEntity.setClient(this.clientEntity);
            expenseEntity.setCategory(categoryEntity);
            response.add(expenseEntity);
        }
        return response;
    }
}