package hr.scuric.dewallet.budget.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import hr.scuric.dewallet.budget.enums.Period;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseStatistics {
    @JsonProperty(value = "period")
    private Period period;

    @JsonProperty(value = "month")
    private Integer month;

    @JsonProperty(value = "year")
    private Integer year;

    @JsonProperty(value = "category")
    private CategoryResponse category;

    @JsonProperty(value = "totals")
    private TotalsResponse totals;

    @JsonProperty(value = "overview")
    private Map<String, TotalsResponse> overview;

    @JsonProperty(value = "overviewForCategory")
    private Map<String, TotalsResponse> overviewForCategory;

    @JsonProperty(value = "overviewPerMonthPerCategory")
    private Map<String, Map<String, TotalsResponse>> overviewPerMonthPerCategory;
}
