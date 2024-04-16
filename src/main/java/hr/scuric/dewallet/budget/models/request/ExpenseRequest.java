package hr.scuric.dewallet.budget.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import hr.scuric.dewallet.budget.enums.ExpenseType;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseRequest {
    @JsonProperty(value = "type")
    private ExpenseType type;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("description")
    private String description;

    @JsonProperty("categoryId")
    private Long categoryId;
}
