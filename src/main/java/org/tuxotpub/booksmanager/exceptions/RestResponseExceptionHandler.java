package org.tuxotpub.booksmanager.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.InvocationTargetException;

import static org.springframework.http.HttpStatus.*;

/**
 * Created by tuxsamo.
 */
@Slf4j
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    public RestResponseExceptionHandler() {
        super();
    }

    @Override
    protected final ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException e, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        log.debug(BAD_REQUEST.getReasonPhrase() + ": {}", e);
        String bodyMessage = getMessage(BAD_REQUEST);
        return new ResponseEntity<>(bodyMessage, new HttpHeaders(), BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAnyException(final Exception e, final WebRequest request) {
        log.error(BAD_REQUEST.getReasonPhrase()+ ": " + e.getMessage(), e);
        String bodyMessage = getMessage(BAD_REQUEST);
        return new ResponseEntity<>(bodyMessage, new HttpHeaders(), BAD_REQUEST);
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class, NumberFormatException.class, ConstraintViolationException.class, DataIntegrityViolationException.class})
    public final ResponseEntity<Object> handleBadRequest(final RuntimeException e, final WebRequest request) {
        log.debug(BAD_REQUEST.getReasonPhrase()+ ": {}", e);
        String bodyMessage = getMessage(BAD_REQUEST);
        return new ResponseEntity<>(bodyMessage, new HttpHeaders(), BAD_REQUEST);
    }

    @ExceptionHandler({EntityNotFoundException.class, ResourceNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(Exception exception, WebRequest request){
        log.warn("Resource not found: {} {}", exception.getMessage(), request.getDescription(true));
        String bodyMessage = getMessage(NOT_FOUND);
        return new ResponseEntity<>(bodyMessage, new HttpHeaders(), NOT_FOUND);
    }

    @ExceptionHandler({InvalidDataAccessApiUsageException.class, DataAccessException.class,
            OptimisticLockingFailureException.class})
    protected ResponseEntity<Object> handleConflict(final RuntimeException e, final WebRequest request) {
        log.warn(CONFLICT + ": {}", e.getMessage());
        String bodyMessage = getMessage(CONFLICT);
        return new ResponseEntity<>(bodyMessage, new HttpHeaders(), CONFLICT);
    }

    @ExceptionHandler({InvalidMimeTypeException.class, InvalidMediaTypeException.class})
    protected ResponseEntity<Object> handleInvalidMimeTypeException(final IllegalArgumentException e, final WebRequest request) {
        log.warn(UNSUPPORTED_MEDIA_TYPE + ": {}", e.getMessage());
        String bodyMessage = getMessage(UNSUPPORTED_MEDIA_TYPE);
        return new ResponseEntity<>(bodyMessage, new HttpHeaders(), UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler({InvocationTargetException.class, NullPointerException.class, IllegalArgumentException.class,
        IllegalStateException.class, ConversionFailedException.class, ClassCastException.class})
    public ResponseEntity<Object> handle500s(final RuntimeException e, final WebRequest request) {
        logger.error(INTERNAL_SERVER_ERROR.getReasonPhrase(), e);
        String bodyMessage = getMessage(INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(bodyMessage, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }

    private String getMessage(final HttpStatus status) {
        return status.value() + " " + status.getReasonPhrase();
    }
}