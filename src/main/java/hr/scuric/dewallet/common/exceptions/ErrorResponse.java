package hr.scuric.dewallet.common.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private HttpStatus status = HttpStatus.OK;
    private int code = 200;

    private LocalDateTime timestamp = LocalDateTime.now();
    private String message = "OK";
    private List<String> errors;


    public ErrorResponse() {
    }

    public ErrorResponse(final Messages message) {
        this.status = message.getHttpStatus();
        this.code = message.getCode();
        this.message = message.getMessage();
        this.errors = null;
    }

    public ErrorResponse(final HttpStatus httpStatus, final int code, final String message) {
        this.status = httpStatus;
        this.code = code;
        this.message = message;
        this.errors = null;
    }

    public ErrorResponse(final HttpStatus httpStatus, final int code, final String message, final List<String> errors) {
        this.status = httpStatus;
        this.code = code;
        this.message = message;
        this.errors = errors;
    }
}
