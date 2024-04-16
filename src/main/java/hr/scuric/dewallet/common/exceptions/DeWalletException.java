package hr.scuric.dewallet.common.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeWalletException extends Exception {
    private final Messages messages;

    private int httpStatusCode = -1;

    private String responseBody = null;

    private List<String> errors = null;

    public DeWalletException(Messages messages) {
        super(messages.getMessage());
        this.messages = messages;
    }

    public DeWalletException(String message, Messages messages, int httpStatusCode, String responseBody) {
        super(message);
        this.messages = messages;
        this.httpStatusCode = httpStatusCode;
        this.responseBody = responseBody;
    }

    public DeWalletException(Messages messages, List<String> errors) {
        super(messages.getMessage());
        this.messages = messages;
        this.errors = errors;
    }
}
