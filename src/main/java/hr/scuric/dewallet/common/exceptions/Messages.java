package hr.scuric.dewallet.common.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public enum Messages {
    /**
     * General http status codes.
     */
    //@formatter:off
    OK(HttpStatus.OK, 200, "OK"),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "{1}"),

    /**
     * General status codes start with 100x:
     * <p>
     * 1000 - ENUM_ERROR
     * 1001 - EMPTY_PARAM
     * 1002 - BAD_REQUEST_CONSTRAINT_ERROR
     * 1003 - NO_DATA
     */

    INVALID_INPUT_TYPE(HttpStatus.BAD_REQUEST, 1000, "Input data '{0}' should be of a valid type/value  '{1}' and '{2}' isn't!"),
    EMPTY_PARAM(HttpStatus.BAD_REQUEST, 1001, "At least one parameter is invalid or not supplied"),
    BAD_REQUEST_CONSTRAINT_ERROR(HttpStatus.BAD_REQUEST, 1002, "Unable to perform this action."),
    NO_DATA(HttpStatus.BAD_REQUEST, 1003, "Data not found."),

    /**
     * General status codes start with 110x:
     * <p>
     * 1100 - ENTITY_NOT_FOUND
     * 1101 - TASK_NOT_VALID
     */
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, 1100, "Entity not found."),
    TASK_NOT_VALID(HttpStatus.BAD_REQUEST, 1101, "Task is not valid."),
    ENTITY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 1102, "Entity already exists."),

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
        return httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage(final String value) {
        return message.replace("{1}", value);
    }

    public Messages getMessageReplace(final List<String> values) {
        for (final String replacement : values) {
            message = message.replace("{" + values.indexOf(replacement) + "}", replacement);
        }
        return this;
    }

    public Messages getMessageReplace(final String value) {
        message = message.replace("{1}", value);
        return this;
    }
}