package org.jobs.manager.backend.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
        log.error("Exception catched {}",request.getDescription(true), ex);
        return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.PARTIAL_CONTENT);
    }
}
