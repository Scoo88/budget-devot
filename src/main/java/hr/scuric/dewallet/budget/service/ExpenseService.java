package hr.scuric.dewallet.budget.service;

import hr.scuric.dewallet.budget.enums.ExpenseType;
import hr.scuric.dewallet.budget.enums.Period;
import hr.scuric.dewallet.budget.models.entity.CategoryEntity;
import hr.scuric.dewallet.budget.models.entity.ExpenseEntity;
import hr.scuric.dewallet.budget.models.request.ExpenseRequest;
import hr.scuric.dewallet.budget.models.response.ExpenseResponse;
import hr.scuric.dewallet.budget.models.response.ExpenseStatistics;
import hr.scuric.dewallet.budget.repository.ExpenseRepository;
import hr.scuric.dewallet.budget.repository.ExpenseSpecification;
import hr.scuric.dewallet.client.models.entity.ClientEntity;
import hr.scuric.dewallet.client.service.ClientService;
import hr.scuric.dewallet.common.exceptions.DeWalletException;
import hr.scuric.dewallet.common.exceptions.Messages;
import hr.scuric.dewallet.common.security.IAuthentificationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryService categoryService;
    private final ClientService clientService;
    private final IAuthentificationFacade authentication;

    public ExpenseResponse insertExpense(ExpenseRequest request) throws DeWalletException {
        ClientEntity clientEntity = this.authentication.getClientEntity();
        CategoryEntity categoryEntity = this.categoryService.getById(request.getCategoryId());
        ExpenseEntity entity = ExpenseEntity.fromRequest(request, categoryEntity, clientEntity);
        this.expenseRepository.saveAndFlush(entity);
        this.clientService.handleBalance(Collections.singletonList(entity), null);
        return ExpenseResponse.fromEntity(entity);
    }

    public List<ExpenseResponse> getExpenses() {
        List<ExpenseEntity> all = this.expenseRepository.findAllByClientId(this.authentication.getPrincipalId());
        List<ExpenseResponse> response = new ArrayList<>();
        all.forEach(entity -> {
            response.add(ExpenseResponse.fromEntity(entity));
        });
        return response;
    }

    public ExpenseResponse getExpense(Long id) throws DeWalletException {
        return ExpenseResponse.fromEntity(this.getById(id));
    }

    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) throws DeWalletException {
        ExpenseEntity entity = this.getById(id);
        BigDecimal oldAmount = entity.getAmount();

        if (Objects.nonNull(request.getAmount())) {
            entity.setAmount(request.getAmount());
        }

        if (Objects.nonNull(request.getDescription())) {
            entity.setDescription(request.getDescription());
        }

        if (Objects.nonNull(request.getCategoryId())) {
            entity.setCategory(this.categoryService.getById(request.getCategoryId()));
        }

        if (Objects.nonNull(request.getDescription())) {
            entity.setType(request.getType());
        }

        this.expenseRepository.saveAndFlush(entity);

        this.clientService.handleBalance(Collections.singletonList(entity), oldAmount);

        return ExpenseResponse.fromEntity(entity);
    }

    public ExpenseResponse updateExpenseStatus(Long id, boolean active) throws DeWalletException {
        ExpenseEntity entity = this.getById(id);
        entity.setIsActive(active);
        this.expenseRepository.saveAndFlush(entity);
        this.clientService.handleBalance(Collections.singletonList(entity), null);
        return ExpenseResponse.fromEntity(entity);
    }

    public HttpStatus deleteExpense(Long id) throws DeWalletException {
        ExpenseEntity entity = this.getById(id);
        entity.setIsActive(false);
        this.expenseRepository.delete(entity);
        this.clientService.handleBalance(Collections.singletonList(entity), null);
        return HttpStatus.NO_CONTENT;
    }

    public List<ExpenseResponse> filterExpenses(Long categoryId, BigDecimal minAmount, BigDecimal maxAmount, LocalDate startDate, LocalDate endDate, ExpenseType type) {
        Specification<ExpenseEntity> specification = ExpenseSpecification.filterBy(categoryId, minAmount, maxAmount, startDate, endDate, type);
        List<ExpenseEntity> expenseEntities = this.expenseRepository.findAll(specification);
        List<ExpenseResponse> response = new ArrayList<>();
        for (ExpenseEntity entity : expenseEntities) {
            response.add(ExpenseResponse.fromEntity(entity));
        }
        return response;
    }

    public ExpenseStatistics getStatistics(Period period, Optional<Integer> month, Integer year, Optional<Long> categoryId) throws DeWalletException {
        ExpenseStatistics response = new ExpenseStatistics();
        response.setPeriod(period);
        response.setMonth(month.orElse(null));
        response.setYear(year);

        Map<String, LocalDate> dates = this.calculatePeriod(period, month, year);
//        this.expenseRepository.getStatistics(dates.keySet())
//        response.setType();
//        response.setTotal();
        return null;
    }

    private ExpenseEntity getById(Long id) throws DeWalletException {
        Optional<ExpenseEntity> optionalExpense = this.expenseRepository.findByIdAndClientId(id, this.authentication.getPrincipalId());
        if (optionalExpense.isEmpty()) {
            throw new DeWalletException(Messages.ENTITY_NOT_FOUND);
        }
        return optionalExpense.get();
    }

    private Map<String, LocalDate> calculatePeriod(Period period, Optional<Integer> month, Integer year) throws DeWalletException {
        LocalDate start, end;

        switch (period) {
            case MONTH:
                if (month.isPresent()) {
                    start = LocalDate.of(year, month.get(), 1);
                    end = start.plusMonths(1).minusDays(1);
                } else {
                    throw new DeWalletException(Messages.INVALID_INPUT_TYPE);
                }
                break;
            case YEAR:
                start = LocalDate.of(year, 1, 1);
                end = start.plusYears(1).minusDays(1);
                break;
            case FIRST_QUARTER:
                start = LocalDate.of(year, 1, 1);
                end = LocalDate.of(year, 3, 31);
                break;
            case SECOND_QUARTER:
                start = LocalDate.of(year, 4, 1);
                end = LocalDate.of(year, 6, 30);
                break;
            case THIRD_QUARTER:
                start = LocalDate.of(year, 7, 1);
                end = LocalDate.of(year, 9, 30);
                break;
            case FOURTH_QUARTER:
                start = LocalDate.of(year, 10, 1);
                end = LocalDate.of(year, 12, 31);
                break;
            default:
                throw new DeWalletException(Messages.INVALID_INPUT_TYPE);
        }
        return Map.of("start", start, "end", end);
    }
}
