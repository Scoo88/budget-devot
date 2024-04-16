package hr.scuric.dewallet.budget.repository;

import hr.scuric.dewallet.budget.enums.ExpenseType;

import java.math.BigDecimal;

public interface ExpensesPerMonth {
    String getMonth();

    ExpenseType getType();

    BigDecimal getTotal();
}
