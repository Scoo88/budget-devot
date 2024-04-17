package hr.scuric.dewallet.budget.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import hr.scuric.dewallet.budget.enums.ExpenseType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseRequest {
    @JsonProperty(value = "type")
    @NotNull
    private ExpenseType type;

    @JsonProperty("amount")
    @NotNull
    @Min(value = 0)
    private BigDecimal amount;

    @JsonProperty("description")
    @Size(min = 1, max = 255)
    private String description;

    @JsonProperty("categoryId")
    @NotNull
    private Long categoryId;
}
