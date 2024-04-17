package hr.scuric.dewallet.budget.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import hr.scuric.dewallet.budget.models.entity.CategoryEntity;
import hr.scuric.dewallet.common.models.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class CategoryResponse extends BaseDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("isActive")
    private Boolean isActive;

    public static CategoryResponse fromEntity(CategoryEntity entity) {
        CategoryResponse response = new CategoryResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setCreatedAt(entity.getCreatedAt());
        response.setIsActive(entity.getIsActive());
        return response;
    }
}
