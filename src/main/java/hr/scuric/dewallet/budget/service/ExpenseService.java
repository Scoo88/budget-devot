package hr.scuric.dewallet.budget.service;

import hr.scuric.dewallet.budget.enums.ExpenseType;
import hr.scuric.dewallet.budget.enums.Period;
import hr.scuric.dewallet.budget.models.entity.CategoryEntity;
import hr.scuric.dewallet.budget.models.entity.ExpenseEntity;
import hr.scuric.dewallet.budget.models.request.ExpenseRequest;
import hr.scuric.dewallet.budget.models.response.ExpenseResponse;
import hr.scuric.dewallet.budget.models.response.ExpenseStatistics;
import hr.scuric.dewallet.budget.models.response.TotalsResponse;
import hr.scuric.dewallet.budget.repository.ExpenseRepository;
import hr.scuric.dewallet.budget.repository.ExpenseSpecification;
import hr.scuric.dewallet.budget.repository.ExpenseStatisticsView;
import hr.scuric.dewallet.budget.repository.ExpensesPerMonth;
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
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static hr.scuric.dewallet.budget.enums.Period.MONTH;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryService categoryService;
    private final ClientService clientService;
    private final IAuthentificationFacade authentication;
    private final MathContext MC_4 = new MathContext(4, RoundingMode.HALF_UP);

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
        all.forEach(entity -> response.add(ExpenseResponse.fromEntity(entity)));
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
        Specification<ExpenseEntity> specification = ExpenseSpecification.filterBy(this.authentication.getPrincipalId(), categoryId, minAmount, maxAmount, startDate, endDate, type);
        List<ExpenseEntity> expenseEntities = this.expenseRepository.findAll(specification);
        List<ExpenseResponse> response = new ArrayList<>();
        for (ExpenseEntity entity : expenseEntities) {
            response.add(ExpenseResponse.fromEntity(entity));
        }
        return response;
    }

    public ExpenseStatistics getStatistics(Period period, Integer month, Integer year, LocalDate startDate, LocalDate endDate, Long categoryId) throws DeWalletException {
        ExpenseStatistics response = new ExpenseStatistics();
        response.setPeriod(period);
        response.setMonth(month);
        response.setYear(year);

        if (Objects.nonNull(categoryId)) {
            response.setCategory(this.categoryService.getCategory(categoryId));
        }

        LocalDateTime start;
        LocalDateTime end;
        if (Objects.nonNull(period)) {
            Map<String, LocalDateTime> dates = this.calculatePeriod(period, month, year);
            start = dates.get("start");
            end = dates.get("end");
        } else {
            start = startDate.atStartOfDay();
            end = endDate.plusDays(1).atStartOfDay();
        }

        List<ExpenseStatisticsView> statistics = this.expenseRepository.getStatistics(start, end);
        if (!statistics.isEmpty()) {
            TotalsResponse totals = new TotalsResponse();

            for (ExpenseStatisticsView stat : statistics) {
                setTotalByType(totals, stat);
            }

            this.calculatePercentages(totals);
            response.setTotals(totals);
        }

        if (Objects.nonNull(period) && !period.equals(MONTH)) {
            Map<String, TotalsResponse> overview = new HashMap<>();

            List<ExpensesPerMonth> expensesPerMonths = this.expenseRepository.getOverview(start, end);
            expensesPerMonths.forEach(expensesPerMonth -> {
                String mapMonth = expensesPerMonth.getMonth();
                BigDecimal total = expensesPerMonth.getTotal();
                if (!overview.containsKey(mapMonth)) {
                    TotalsResponse newTotals = new TotalsResponse();
                    setTotalByType(expensesPerMonth.getType(), newTotals, total);
                    overview.put(mapMonth, newTotals);
                } else {
                    TotalsResponse totals = overview.get(mapMonth);
                    setTotalByType(expensesPerMonth.getType(), totals, total);
                    this.calculatePercentages(totals);
                }
            });
            response.setOverview(overview);
        }
        return response;
    }

    private ExpenseEntity getById(Long id) throws DeWalletException {
        Optional<ExpenseEntity> optionalExpense = this.expenseRepository.findByIdAndClientId(id, this.authentication.getPrincipalId());
        if (optionalExpense.isEmpty()) {
            throw new DeWalletException(Messages.ENTITY_NOT_FOUND);
        }
        return optionalExpense.get();
    }

    private Map<String, LocalDateTime> calculatePeriod(Period period, Integer month, Integer year) throws DeWalletException {
        LocalDate start;
        LocalDate end;

        switch (period) {
            case MONTH -> {
                if (Objects.nonNull(month)) {
                    start = LocalDate.of(year, month, 1);
                    end = start.plusMonths(1).minusDays(1);
                } else {
                    throw new DeWalletException(Messages.INVALID_INPUT_TYPE);
                }
            }
            case YEAR -> {
                start = LocalDate.of(year, 1, 1);
                end = start.plusYears(1).minusDays(1);
            }
            case Q1 -> {
                start = LocalDate.of(year, 1, 1);
                end = LocalDate.of(year, 3, 31);
            }
            case Q2 -> {
                start = LocalDate.of(year, 4, 1);
                end = LocalDate.of(year, 6, 30);
            }
            case Q3 -> {
                start = LocalDate.of(year, 7, 1);
                end = LocalDate.of(year, 9, 30);
            }
            case Q4 -> {
                start = LocalDate.of(year, 10, 1);
                end = LocalDate.of(year, 12, 31);
            }
            default -> throw new DeWalletException(Messages.INVALID_INPUT_TYPE);
        }
        return Map.of("start", start.atStartOfDay(), "end", end.plusDays(1).atStartOfDay());
    }

    private static void setTotalByType(ExpenseType expensesPerMonth, TotalsResponse newTotals, BigDecimal total) {
        switch (expensesPerMonth) {
            case INPUT -> newTotals.setTotalEarned(total);
            case OUTPUT -> newTotals.setTotalSpent(total);
        }
    }

    private static void setTotalByType(TotalsResponse totals, ExpenseStatisticsView stat) {
        BigDecimal total = stat.getTotal();
        setTotalByType(stat.getType(), totals, total);
    }

    private void calculatePercentages(TotalsResponse totals) {
        if (Objects.nonNull(totals.getTotalSpent()) && Objects.nonNull(totals.getTotalEarned())) {
            BigDecimal sum = totals.getTotalSpent().add(totals.getTotalEarned());
            totals.setTotalEarnedPercent(totals.getTotalEarned().divide(sum, this.MC_4).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
            totals.setTotalSpentPercent(totals.getTotalSpent().divide(sum, this.MC_4).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
        }
    }
}
