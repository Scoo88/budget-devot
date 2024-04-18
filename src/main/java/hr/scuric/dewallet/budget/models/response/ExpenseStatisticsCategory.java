package hr.scuric.dewallet.budget.models.response;

import hr.scuric.dewallet.budget.enums.ExpenseType;

import java.math.BigDecimal;

public interface ExpenseStatisticsCategory {
    String getMonth();

    ExpenseType getType();

    BigDecimal getTotal();

    Long getCategory();
}
