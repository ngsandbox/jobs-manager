package org.jobs.manager.backend.handlers;

import lombok.extern.slf4j.Slf4j;
import org.jobs.manager.backend.models.ErrorDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {


    private MessageSource messageSource;

    @Autowired
    public RestResponseEntityExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleFaultdException(Exception ex, WebRequest request) {
        log.error("Exception catched {}", request.getDescription(true), ex);
        return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        List<ErrorDetails> errorDetails = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::getErrorDetails)
                .collect(Collectors.toList());

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    private String resolveLocalizedErrorMessage(FieldError fieldError) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        String localizedErrorMessage = messageSource.getMessage(fieldError, currentLocale);

        //If the message was not found, return the most accurate field error code instead.
        //You can remove this check if you prefer to get the default error message.
        if (!localizedErrorMessage.equals(fieldError.getDefaultMessage())) {
            String[] fieldErrorCodes = fieldError.getCodes();
            localizedErrorMessage = fieldErrorCodes[0];
        }

        return localizedErrorMessage;
    }

    private ErrorDetails getErrorDetails(FieldError fieldError) {
        String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
        return new ErrorDetails(new Date(), fieldError.getField(), localizedErrorMessage);
    }
}
