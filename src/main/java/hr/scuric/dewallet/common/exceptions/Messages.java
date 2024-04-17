package hr.scuric.dewallet.common.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public enum Messages {
    //@formatter:off
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "{1}"),
    INVALID_INPUT_TYPE(HttpStatus.BAD_REQUEST, 1000, "Input data '{0}' should be of a valid type/value  '{1}' and '{2}' isn't!"),
    EMPTY_PARAM(HttpStatus.BAD_REQUEST, 1001, "At least one parameter is invalid or not supplied"),
    BAD_REQUEST_CONSTRAINT_ERROR(HttpStatus.BAD_REQUEST, 1002, "Unable to perform this action."),
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, 1100, "Entity not found."),
    BALANCE_TOO_LOW(HttpStatus.BAD_REQUEST, 1103, "Not enough credits."),
    ;
    //@formatter:on

    private final HttpStatus httpStatus;
    private final int code;
    private String message;

    Messages(final HttpStatus httpStatus, final int code, final String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getMessage(final String value) {
        return this.message.replace("{1}", value);
    }

    public Messages getMessageReplace(final List<String> values) {
        for (final String replacement : values) {
            this.message = this.message.replace("{" + values.indexOf(replacement) + "}", replacement);
        }
        return this;
    }

    public Messages getMessageReplace(final String value) {
        this.message = this.message.replace("{1}", value);
        return this;
    }
}