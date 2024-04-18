package hr.scuric.dewallet.budget.service;

import hr.scuric.dewallet.budget.enums.ExpenseType;
import hr.scuric.dewallet.budget.enums.Period;
import hr.scuric.dewallet.budget.models.response.TotalsResponse;
import hr.scuric.dewallet.budget.repository.ExpenseStatisticsView;
import hr.scuric.dewallet.common.exceptions.DeWalletException;
import hr.scuric.dewallet.common.exceptions.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticService {
    public Map<String, LocalDateTime> calculatePeriod(Period period, Integer month, Integer year) throws DeWalletException {
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

    public static void setTotalByType(ExpenseType expensesPerMonth, TotalsResponse newTotals, BigDecimal total) {
        switch (expensesPerMonth) {
            case INPUT -> newTotals.setTotalEarned(total);
            case OUTPUT -> newTotals.setTotalSpent(total);
        }
    }

    public static void setTotalByType(TotalsResponse totals, ExpenseStatisticsView stat) {
        BigDecimal total = stat.getTotal();
        setTotalByType(stat.getType(), totals, total);
    }

    public void mapHandler(Map<String, TotalsResponse> overview, String key, BigDecimal total, ExpenseType type) {
        if (!overview.containsKey(key)) {
            mapCalculationNew(type, total, overview, key);
        } else {
            mapCalculationExisting(overview, key, type, total);
        }
    }

    public static void mapCalculationExisting(Map<String, TotalsResponse> overview, String mapMonth, ExpenseType expensesPerMonth, BigDecimal total) {
        TotalsResponse totals = overview.get(mapMonth);
        setTotalByType(expensesPerMonth, totals, total);
        calculateBalance(totals);
    }

    public static void mapCalculationNew(ExpenseType expensesPerMonth, BigDecimal total, Map<String, TotalsResponse> overview, String mapMonth) {
        TotalsResponse newTotals = new TotalsResponse();
        StatisticService.setTotalByType(expensesPerMonth, newTotals, total);
        overview.put(mapMonth, newTotals);
    }

    public static void calculateBalance(TotalsResponse totals) {
        if (Objects.nonNull(totals.getTotalSpent()) && Objects.nonNull(totals.getTotalEarned())) {
            BigDecimal balance = totals.getTotalEarned().subtract(totals.getTotalSpent()).setScale(2, RoundingMode.HALF_UP);
            totals.setBalanace(balance);
        }
    }
}
