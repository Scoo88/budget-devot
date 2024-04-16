package hr.scuric.dewallet.common.exceptions;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleArgumentException(final IllegalArgumentException e) {
        logException(e);

        final ErrorResponse errorResponse = new ErrorResponse(Messages.EMPTY_PARAM);
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleTypeMismatch(final MethodArgumentTypeMismatchException ex) {
        logException(ex);
        final Messages messages = Messages.INVALID_INPUT_TYPE;

        final String name = ex.getName();
        final String type = Objects.requireNonNull(ex.getRequiredType()).getSimpleName();
        final Object value = Objects.requireNonNull(ex.getValue()).toString();

        String tempMine = messages.getMessage();
        final ErrorResponse responseBase = new ErrorResponse();
        responseBase.setCode(messages.getCode());

        tempMine = getReplaceViaList(tempMine, Arrays.asList(name, type, value.toString()));

        responseBase.setMessage(tempMine);
        responseBase.setStatus(messages.getHttpStatus());
        return new ResponseEntity<>(responseBase, new HttpHeaders(), responseBase.getStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            final DataIntegrityViolationException e) {

        final ErrorResponse errorResponse = new ErrorResponse(Messages.BAD_REQUEST_CONSTRAINT_ERROR);

        if (e.getCause() instanceof ConstraintViolationException cve) {
            final List<String> errors = new ArrayList<>();

            String regex = "ERROR: (.*?)\"";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(cve.getMessage());

            if (matcher.find()) {
                errors.add(matcher.group(1));
            }
            errorResponse.setErrors(errors);
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElementException(final NoSuchElementException ex) {
        logException(ex);

        final ErrorResponse responseBase = new ErrorResponse(HttpStatus.NOT_FOUND, 404, ex.getMessage());

        return new ResponseEntity<>(responseBase, new HttpHeaders(), responseBase.getStatus());
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<?> handleMethodNotAllowedException(final MethodNotAllowedException ex) {
        logException(ex);

        final ErrorResponse responseBase = new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, 405, ex.getMessage());

        return new ResponseEntity<>(responseBase, new HttpHeaders(), responseBase.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(final EntityNotFoundException ex) {
        logException(ex);

        final ErrorResponse responseBase = new ErrorResponse(HttpStatus.NOT_FOUND, 404, ex.getMessage());

        return new ResponseEntity<>(responseBase, new HttpHeaders(), responseBase.getStatus());
    }


    @ExceptionHandler({DeWalletException.class})
    public ResponseEntity<Object> handleBudgetException(final DeWalletException ex) {
        ErrorResponse responseBase = new ErrorResponse(ex.getMessages());

        if (Objects.nonNull(ex.getErrors())) {
            responseBase.setErrors(ex.getErrors());
        }

        return new ResponseEntity<>(responseBase, new HttpHeaders(), responseBase.getStatus());
    }


    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(final Exception ex) {
        logException(ex);

        final ErrorResponse responseBase = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 500, ex.getMessage());

        return new ResponseEntity<>(responseBase, new HttpHeaders(), responseBase.getStatus());
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatusCode status,
                                                                  final WebRequest request) {
        logException(ex);

        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final ErrorResponse responseBase = new ErrorResponse(HttpStatus.BAD_REQUEST, 400, "Validation Failed", errors);
        return handleExceptionInternal(ex, responseBase, headers, responseBase.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        logException(ex);
        final List<String> errors = new ArrayList<>();

        if (ex.getMessage().contains(";")) {
            errors.add(ex.getMessage().split(";")[0]);
        } else {
            errors.add(ex.getMessage());
        }

        final ErrorResponse responseBase = new ErrorResponse(HttpStatus.BAD_REQUEST, 400, "Validation Failed", errors);
        return handleExceptionInternal(ex, responseBase, headers, responseBase.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex,
                                                                     final HttpHeaders headers,
                                                                     final HttpStatusCode status,
                                                                     final WebRequest request) {
        logException(ex);

        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));

        final ErrorResponse responseBase = new ErrorResponse(HttpStatus.BAD_REQUEST,
                415,
                builder.substring(0, builder.length() - 2));
        return new ResponseEntity<>(responseBase, new HttpHeaders(), responseBase.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatusCode status,
            final WebRequest request) {
        logException(ex);

        final String error = ex.getParameterName() + " parameter is missing";
        final ErrorResponse responseBase = new ErrorResponse(HttpStatus.BAD_REQUEST, 400, error);
        return new ResponseEntity<>(responseBase, responseBase.getStatus());
    }


    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatusCode status,
            final WebRequest request) {
        logException(ex);

        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        final ErrorResponse responseBase = new ErrorResponse(HttpStatus.BAD_REQUEST, 405, builder.toString());
        return new ResponseEntity<>(responseBase, new HttpHeaders(), responseBase.getStatus());
    }

    public static String getReplaceViaList(String temp, List<String> values) {
        for (String replacement : values) {
            temp = temp.replace("{" + values.indexOf(replacement) + "}", replacement);
        }
        return temp;
    }

//    @ExceptionHandler(UsernameNotFoundException.class)
//    public ResponseEntity<BudgetException> handleUsernameNotFoundException(UsernameNotFoundException ex) {
//        BudgetException budgetException = new BudgetException(Messages.ENTITY_NOT_FOUND);
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(budgetException);
//    }

    private void logException(Throwable e) {
        log.error("Unhandled exception!", e);
    }
}
