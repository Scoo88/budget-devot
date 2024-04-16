package hr.scuric.dewallet.budget.models.response;

import hr.scuric.dewallet.budget.enums.ExpenseType;
import hr.scuric.dewallet.budget.enums.Period;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExpenseStatistics {
    private Period period;
    private Integer month;
    private Integer year;
    private CategoryResponse category;
    private ExpenseType type;
    private BigDecimal total;
}
