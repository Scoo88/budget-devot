package hr.scuric.dewallet.budget.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import hr.scuric.dewallet.budget.enums.ExpenseType;
import hr.scuric.dewallet.budget.models.entity.ExpenseEntity;
import hr.scuric.dewallet.common.model.BaseDto;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseResponse extends BaseDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("type")
    private ExpenseType type;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category")
    private CategoryResponse category;

    public static ExpenseResponse fromEntity(ExpenseEntity entity) {
        ExpenseResponse response = new ExpenseResponse();
        response.setId(entity.getId());
        response.setType(entity.getType());
        response.setAmount(entity.getAmount());
        response.setDescription(entity.getDescription());
        response.setCategory(CategoryResponse.fromEntity(entity.getCategory()));
        response.setCreatedAt(entity.getCreatedAt());
        response.setIsActive(entity.getIsActive());
        return response;

    }
}
