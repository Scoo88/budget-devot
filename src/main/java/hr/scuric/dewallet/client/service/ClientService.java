package hr.scuric.dewallet.client.service;

import hr.scuric.dewallet.budget.enums.ExpenseType;
import hr.scuric.dewallet.budget.models.entity.ExpenseEntity;
import hr.scuric.dewallet.budget.models.request.CategoryRequest;
import hr.scuric.dewallet.budget.service.CategoryService;
import hr.scuric.dewallet.client.models.entity.ClientEntity;
import hr.scuric.dewallet.client.models.request.ClientRequest;
import hr.scuric.dewallet.client.models.response.ClientResponse;
import hr.scuric.dewallet.client.repository.ClientRepository;
import hr.scuric.dewallet.common.exceptions.DeWalletException;
import hr.scuric.dewallet.common.exceptions.Messages;
import hr.scuric.dewallet.common.security.IAuthentificationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final CategoryService categoryService;
    private final PasswordEncoder passwordEncoder;
    private final IAuthentificationFacade authentication;

    public ClientResponse registerClient(ClientRequest request) {
        String password = this.passwordEncoder.encode(request.getPassword());
        ClientEntity entity = ClientEntity.fromRequest(request, password);
        this.clientRepository.saveAndFlush(entity);

        List<CategoryRequest> categoryRequests = new ArrayList<>();
        categoryRequests.add(new CategoryRequest("Salary"));
        categoryRequests.add(new CategoryRequest("Utilities"));
        categoryRequests.add(new CategoryRequest("Food"));
        categoryRequests.add(new CategoryRequest("Accommodation"));
        categoryRequests.add(new CategoryRequest("Car"));
        categoryRequests.add(new CategoryRequest("Other"));
        categoryRequests.forEach(categoryRequest -> this.categoryService.insertCategory(categoryRequest, Optional.of(entity)));

        return ClientResponse.fromEntity(entity);
    }

    public ClientResponse getClient() throws DeWalletException {
        ClientEntity entity = this.getById(this.authentication.getPrincipalId());
        return ClientResponse.fromEntity(entity);
    }

    public ClientResponse updateClient(ClientRequest request) throws DeWalletException {
        ClientEntity entity = this.getById(this.authentication.getPrincipalId());

        if (Objects.nonNull(request.getFirstName())) {
            entity.setFirstName(request.getFirstName());
        }

        if (Objects.nonNull(request.getLastName())) {
            entity.setLastName(request.getLastName());
        }

        if (Objects.nonNull(request.getPhone())) {
            entity.setPhone(request.getPhone());
        }

        this.clientRepository.saveAndFlush(entity);
        return ClientResponse.fromEntity(entity);
    }

    public HttpStatus deleteClient() throws DeWalletException {
        ClientEntity entity = this.getById(this.authentication.getPrincipalId());
        this.clientRepository.delete(entity);
        return HttpStatus.NO_CONTENT;
    }

    private ClientEntity getById(Long id) throws DeWalletException {
        Optional<ClientEntity> optionalClientEntity = this.clientRepository.findById(id);
        if (optionalClientEntity.isEmpty()) {
            throw new DeWalletException(Messages.ENTITY_NOT_FOUND);
        }
        return optionalClientEntity.get();
    }

    public void handleBalance(List<ExpenseEntity> allExpenses, BigDecimal oldAmount) throws DeWalletException {
        ClientEntity entity = this.getById(this.authentication.getPrincipalId());

        BigDecimal balance = entity.getBalance();

        if (!allExpenses.isEmpty() && Objects.isNull(oldAmount)) {
            for (ExpenseEntity expenseEntity : allExpenses) {
                if (expenseEntity.getType() == ExpenseType.INPUT && expenseEntity.getIsActive()) {
                    balance = balance.add(expenseEntity.getAmount());
                } else if (expenseEntity.getType() == ExpenseType.INPUT) {
                    balance = balance.subtract(expenseEntity.getAmount());
                } else if (expenseEntity.getType() == ExpenseType.OUTPUT && expenseEntity.getIsActive()) {
                    balance = balance.subtract(expenseEntity.getAmount());
                } else if (expenseEntity.getType() == ExpenseType.OUTPUT) {
                    balance = balance.add(expenseEntity.getAmount());
                }
            }
        } else if (!allExpenses.isEmpty()) {
            balance = balance.add(oldAmount.subtract(allExpenses.get(0).getAmount()));
        }

        entity.setBalance(balance);
        this.clientRepository.saveAndFlush(entity);
    }
}
