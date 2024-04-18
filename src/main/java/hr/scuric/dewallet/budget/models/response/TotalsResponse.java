package hr.scuric.dewallet.budget.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TotalsResponse {
    @JsonProperty(value = "totalEarned")
    private BigDecimal totalEarned;

    @JsonProperty(value = "totalEarned[%]")
    private BigDecimal totalEarnedPercent;

    @JsonProperty(value = "totalSpent")
    private BigDecimal totalSpent;

    @JsonProperty(value = "totalSpent[%]")
    private BigDecimal totalSpentPercent;

    @JsonProperty(value = "balance")
    private BigDecimal balanace;
}
