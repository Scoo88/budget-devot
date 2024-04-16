package hr.scuric.dewallet.budget.repository;

import hr.scuric.dewallet.budget.enums.ExpenseType;

import java.math.BigDecimal;

public interface ExpenseStatisticsView {
    ExpenseType getType();

    BigDecimal getTotal();
}
