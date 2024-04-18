package hr.scuric.dewallet.budget.service;

import hr.scuric.dewallet.budget.enums.ExpenseType;
import hr.scuric.dewallet.budget.enums.Period;
import hr.scuric.dewallet.budget.models.entity.CategoryEntity;
import hr.scuric.dewallet.budget.models.entity.ExpenseEntity;
import hr.scuric.dewallet.budget.models.request.ExpenseRequest;
import hr.scuric.dewallet.budget.models.response.ExpenseResponse;
import hr.scuric.dewallet.budget.models.response.ExpenseStatistics;
import hr.scuric.dewallet.budget.models.response.ExpenseStatisticsCategory;
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
    private final StatisticService statisticService;


    public ExpenseResponse insertExpense(ExpenseRequest request) throws DeWalletException {
        ClientEntity clientEntity = this.authentication.getClientEntity();
        CategoryEntity categoryEntity = new CategoryEntity();
        if (Objects.nonNull(request.getCategoryId())) {
            categoryEntity = this.categoryService.getById(request.getCategoryId());
        }
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

    public ExpenseStatistics getStatistics(Period period, Integer month, Integer year, LocalDate startDate, LocalDate endDate) throws DeWalletException {
        ExpenseStatistics response = new ExpenseStatistics();
        Long clientId = this.authentication.getPrincipalId();

        LocalDateTime start;
        LocalDateTime end;
        if (!Objects.nonNull(period)) {
            start = startDate.atStartOfDay();
            end = endDate.plusDays(1).atStartOfDay();
        } else {
            Map<String, LocalDateTime> dates = this.statisticService.calculatePeriod(period, month, year);
            start = dates.get("start");
            end = dates.get("end");
            response.setPeriod(period);
            response.setMonth(month);
            response.setYear(year);
        }

        List<ExpenseStatisticsView> statistics = this.expenseRepository.getTotalForTimerange(clientId, start, end);
        if (!statistics.isEmpty()) {
            TotalsResponse totals = new TotalsResponse();
            statistics.forEach(stat -> StatisticService.setTotalByType(totals, stat));
            StatisticService.calculateBalance(totals);
            response.setTotals(totals);
        }

        if ((Objects.nonNull(period) && !period.equals(MONTH)) || Objects.isNull(period)) {
            Map<String, TotalsResponse> overview = new HashMap<>();
            List<ExpensesPerMonth> expensesPerMonths = this.expenseRepository.getOverviewPerMonth(clientId, start, end);
            expensesPerMonths.forEach(expensesPerMonth -> this.statisticService.mapHandler(overview, expensesPerMonth.getMonth(), expensesPerMonth.getTotal(), expensesPerMonth.getType()));
            response.setOverview(overview);
        }

        Map<String, Map<String, TotalsResponse>> overview = new HashMap<>();
        List<ExpenseStatisticsCategory> overviewMonthCategory = this.expenseRepository.getOverviewPerMonthPerCategory(clientId, start, end);

        for (ExpenseStatisticsCategory expensesPerMonth : overviewMonthCategory) {
            String mapMonth = expensesPerMonth.getMonth();
            String categoryString;

            try {
                categoryString = this.categoryService.getById(expensesPerMonth.getCategory()).getName();
            } catch (DeWalletException e) {
                categoryString = "Uncategorized";
            }

            if (!overview.containsKey(mapMonth)) {
                Map<String, TotalsResponse> categoryMap = new HashMap<>();
                StatisticService.mapCalculationNew(expensesPerMonth.getType(), expensesPerMonth.getTotal(), categoryMap, categoryString);
                overview.put(mapMonth, categoryMap);
            } else {
                Map<String, TotalsResponse> categoryMap = overview.get(mapMonth);
                if (categoryMap.containsKey(categoryString)) {
                    StatisticService.mapCalculationExisting(categoryMap, categoryString, expensesPerMonth.getType(), expensesPerMonth.getTotal());
                } else {
                    StatisticService.mapCalculationNew(expensesPerMonth.getType(), expensesPerMonth.getTotal(), categoryMap, categoryString);
                }
            }
        }
        response.setOverviewPerMonthPerCategory(overview);


        return response;
    }

    private ExpenseEntity getById(Long id) throws DeWalletException {
        Optional<ExpenseEntity> optionalExpense = this.expenseRepository.findByIdAndClientId(id, this.authentication.getPrincipalId());
        if (optionalExpense.isEmpty()) {
            throw new DeWalletException(Messages.ENTITY_NOT_FOUND);
        }
        return optionalExpense.get();
    }


}
