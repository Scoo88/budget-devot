package hr.scuric.dewallet.client.models.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientRequest {
    @JsonProperty("firstName")
    @NotEmpty
    @NotBlank
    @Size(min = 1, max = 255)
    private String firstName;

    @JsonProperty("lastName")
    @NotEmpty
    @NotBlank
    @Size(min = 1, max = 255)
    private String lastName;

    @JsonProperty("dateOfBirth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @JsonProperty("phone")
    @NotEmpty
    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^\\+\\d*")
    private String phone;

    @JsonProperty("email")
    @NotEmpty
    @NotBlank
    @Size(min = 2, max = 255)
    @Email(regexp = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\"
            + ".[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$")
    private String email;

    @JsonProperty("password")
    @NotEmpty
    @NotBlank
    @Size(max = 255)
    private String password;
}
