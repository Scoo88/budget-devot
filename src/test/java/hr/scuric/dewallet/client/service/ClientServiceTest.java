package hr.scuric.dewallet.client.service;

import hr.scuric.dewallet.budget.enums.ExpenseType;
import hr.scuric.dewallet.budget.models.entity.CategoryEntity;
import hr.scuric.dewallet.budget.models.entity.ExpenseEntity;
import hr.scuric.dewallet.budget.service.CategoryService;
import hr.scuric.dewallet.client.models.entity.ClientEntity;
import hr.scuric.dewallet.client.models.request.ClientRequest;
import hr.scuric.dewallet.client.models.response.ClientResponse;
import hr.scuric.dewallet.client.repository.ClientRepository;
import hr.scuric.dewallet.common.exceptions.DeWalletException;
import hr.scuric.dewallet.common.security.IAuthentificationFacade;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @InjectMocks
    private ClientService clientService;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CategoryService categoryService;
    @Mock
    private IAuthentificationFacade authentication;

    private ClientRequest request;
    private ClientEntity entity;
    private ClientResponse clientResponse;
    private Long id;

    @BeforeEach
    void setUp() {
        this.id = 1L;

        this.request = new ClientRequest();
        this.request.setFirstName("First");
        this.request.setLastName("Last");
        this.request.setDateOfBirth(LocalDate.of(1988, 11, 22));
        this.request.setPhone("003859112345678");
        this.request.setEmail("test@email.com");
        this.request.setPassword("test");

        this.entity = new ClientEntity();
        this.entity.setId(this.id);
        this.entity.setFirstName("First");
        this.entity.setLastName("Last");
        this.entity.setDateOfBirth(LocalDate.of(1988, 11, 22));
        this.entity.setPhone("003859112345678");
        this.entity.setEmail("test@email.com");
        this.entity.setPassword(this.passwordEncoder.encode("test"));
        this.entity.setBalance(new BigDecimal("2000"));

        this.clientResponse = new ClientResponse();
        this.clientResponse.setFirstName("First");
        this.clientResponse.setLastName("Last");
        this.clientResponse.setDateOfBirth(LocalDate.of(1988, 11, 22));
        this.clientResponse.setPhone("003859112345678");
        this.clientResponse.setEmail("test@email.com");
        this.clientResponse.setBalance(BigDecimal.valueOf(2000L));
    }

    @Test
    void registerClient() {
        ClientResponse response = this.clientService.registerClient(this.request);
        assertEquals(this.clientResponse.getEmail(), response.getEmail());
    }

    @Test
    void getClient() throws DeWalletException {
        when(this.authentication.getPrincipalId()).thenReturn(this.id);
        when(this.clientRepository.findById(this.id)).thenReturn(Optional.ofNullable(this.entity));

        ClientResponse response = this.clientService.getClient();
        assertEquals(this.clientResponse.getEmail(), response.getEmail());
    }

    @Test
    void getClient_EntityNotFound() {
        when(this.authentication.getPrincipalId()).thenReturn(this.id);
        when(this.clientRepository.findById(this.id)).thenReturn(Optional.empty());

        assertThrows(DeWalletException.class, () -> this.clientService.getClient());
    }

    @Test
    void updateClient() throws DeWalletException {
        when(this.authentication.getPrincipalId()).thenReturn(this.id);
        when(this.clientRepository.findById(this.id)).thenReturn(Optional.ofNullable(this.entity));

        this.request.setPhone("00385977654321");

        ClientResponse response = this.clientService.updateClient(this.request);
        assertEquals(this.request.getPhone(), response.getPhone());
    }

    @Test
    void deleteClient() throws DeWalletException {
        when(this.authentication.getPrincipalId()).thenReturn(this.id);
        when(this.clientRepository.findById(this.id)).thenReturn(Optional.ofNullable(this.entity));

        HttpStatus response = this.clientService.deleteClient();
        assertEquals(HttpStatus.NO_CONTENT, response);
    }

    @Test
    void handleBalance() throws DeWalletException {
        when(this.authentication.getPrincipalId()).thenReturn(this.id);
        when(this.clientRepository.findById(this.id)).thenReturn(Optional.ofNullable(this.entity));

        List<ExpenseEntity> expensesList = this.getExpensesList();

        BigDecimal previousBalance = this.entity.getBalance();
        this.clientService.handleBalance(expensesList, null);
        BigDecimal updatedBalance = this.entity.getBalance();

        assertNotEquals(previousBalance, updatedBalance);
    }

    @Test
    void handleBalance_UpdatedAmount() throws DeWalletException {
        when(this.authentication.getPrincipalId()).thenReturn(this.id);
        when(this.clientRepository.findById(this.id)).thenReturn(Optional.ofNullable(this.entity));

        BigDecimal previousBalance = this.entity.getBalance();
        this.clientService.handleBalance(this.getExpensesList(), BigDecimal.TEN);
        BigDecimal updatedBalance = this.entity.getBalance();

        assertNotEquals(previousBalance, updatedBalance);
    }

    private List<ExpenseEntity> getExpensesList() {
        List<ExpenseEntity> response = new ArrayList<>();

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(this.id);
        categoryEntity.setName("test");
        categoryEntity.setClient(this.entity);

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
            expenseEntity.setClient(this.entity);
            expenseEntity.setCategory(categoryEntity);
            response.add(expenseEntity);
        }
        return response;
    }
}