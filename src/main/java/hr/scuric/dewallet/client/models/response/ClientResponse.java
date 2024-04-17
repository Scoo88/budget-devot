package hr.scuric.dewallet.client.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import hr.scuric.dewallet.client.models.entity.ClientEntity;
import hr.scuric.dewallet.common.models.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientResponse extends BaseDto {
    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("dateOfBirth")
    private LocalDate dateOfBirth;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("balance")
    private BigDecimal balance;

    public static ClientResponse fromEntity(ClientEntity entity) {
        ClientResponse response = new ClientResponse();
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setDateOfBirth(entity.getDateOfBirth());
        response.setPhone(entity.getPhone());
        response.setEmail(entity.getEmail());
        response.setBalance(entity.getBalance());
        response.setCreatedAt(entity.getCreatedAt());
        response.setIsActive(entity.getIsActive());
        return response;
    }
}
