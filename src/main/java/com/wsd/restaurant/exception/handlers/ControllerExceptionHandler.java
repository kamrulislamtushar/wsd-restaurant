package com.wsd.restaurant.exception.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wsd.restaurant.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

  private final MessageSource messageSource;
  Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

  @ExceptionHandler({NullPointerException.class}) // Or whatever exception type you want to handle
  public ResponseEntity<Object> nullPointerException(final NullPointerException ex,
                                                     final WebRequest request) {

    log.info(ex.getClass().getName());
    log.error("Error Log: ", ex);
    final ApiErrorResponse apiError =
        new ApiErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Something Went Wrong.",
            "Null pointer exception"
        );
    return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatusName());
  }

  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
      WebRequest request) {
    log.info(ex.getClass().getName());
    log.warn("Error Log: ", ex);
    final List<String> errors = new ArrayList<String>();
    for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.add(error.getField() + ": " + error.getDefaultMessage());
    }
    for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
      errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
    }
    final ApiErrorResponse apiError =
        new ApiErrorResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
            "Invalid request body!", errors);
    return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatusName());
  }

  @Override
  public ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status,
      WebRequest request) {
    log.info(ex.getClass().getName());
    log.warn("Error Log: ", ex);
    final List<String> errors = new ArrayList<String>();
    String errorMessage = "Request Body is missing or invalid";
    errors.add(errorMessage);
    final ApiErrorResponse apiError =
        new ApiErrorResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
            errorMessage, errors);
    return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatusName());
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex,
                                                          final WebRequest request) {
    log.info(ex.getClass().getName());
    log.warn("Error Log: ", ex);
    final List<String> errors = new ArrayList<String>();
    for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      errors.add(
          violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
              + violation.getMessage());
    }
    final ApiErrorResponse apiError =
        new ApiErrorResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
            ex.getLocalizedMessage(), errors);
    return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatusName());
  }


  @ExceptionHandler({JsonProcessingException.class})
  // Or whatever exception type you want to handle
  public ResponseEntity<Object> handleJsonParsingException(final JsonProcessingException ex,
                                                           final WebRequest request) {
    log.info(ex.getClass().getName());
    log.error("Error Log", ex);
    final ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY,
        HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage(), "Json Processing Exception");
    return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatusName());
  }

  @ExceptionHandler({IllegalArgumentException.class})
  // Or whatever exception type you want to handle
  public ResponseEntity<Object> illegalArgumentException(final IllegalArgumentException ex,
                                                         final WebRequest request) {
    log.error("Exception {}", ex);
    final ApiErrorResponse apiError =
        new ApiErrorResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(), "Something went wrong.");
    return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatusName());
  }

  @ExceptionHandler({IOException.class}) // Or whatever exception type you want to handle
  public ResponseEntity<Object> handleIOException(final IOException ex,
                                                  final WebRequest request) {
    log.info(ex.getClass().getName());
    log.error("Error Log", ex);
    final ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.PRECONDITION_REQUIRED,
        HttpStatus.PRECONDITION_REQUIRED.value(), ex.getMessage(), "IO Exception");
    return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatusName());
  }



  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleAll(final Exception ex, final HttpServletRequest request) {
    log.info(ex.getClass().getName());
    log.warn("Error Log: ", ex);
    final ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something Went Wrong.",
        "Something Went Wrong");
    return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatusName());
  }



  @ExceptionHandler({ValidationException.class})
  public ResponseEntity<Object> handleValidationException(final ValidationException ex,
                                                          final HttpServletRequest request) {
    log.info(ex.getClass().getName());
    log.warn("Error Log: ", ex);
    String message =
        messageSource.getMessage(ex.getMessage(), new String[] {""}, Locale.ENGLISH);
    final ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.value(), message, message);
    return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatusName());
  }



}
