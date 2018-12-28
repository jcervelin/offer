package io.jcervelin.ideas.offer.gateways.http.advices;

import io.jcervelin.ideas.offer.models.exceptions.ErrorResponse;
import io.jcervelin.ideas.offer.models.exceptions.OfferErrorException;
import io.jcervelin.ideas.offer.models.exceptions.OfferNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionControllerAdvice {

    private static final String ERROR_NOT_DEFINITION = "Unidentified error";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception exception) {
        return createMessage(exception, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OfferNotFoundException.class)
    public ResponseEntity<ErrorResponse> noContentFound(final Exception exception) {
        return createMessage(exception, NO_CONTENT);
    }

    @ExceptionHandler(OfferErrorException.class)
    public ResponseEntity<ErrorResponse> offerErrorException(final Exception exception) {
        return createMessage(exception, INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> createMessage(final Exception exception, final HttpStatus httpStatus) {
        log.info("handleException", exception);
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(httpStatus);
        errorResponse.setCode(httpStatus.value());
        errorResponse.setMessage(exception.getMessage().isEmpty() ? ERROR_NOT_DEFINITION : exception.getMessage());
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

}