package io.jcervelin.ideas.offer.gateways.http.advices;

import io.jcervelin.ideas.offer.models.exceptions.ErrorResponse;
import io.jcervelin.ideas.offer.models.exceptions.InvalidOfferException;
import io.jcervelin.ideas.offer.models.exceptions.OfferErrorException;
import io.jcervelin.ideas.offer.models.exceptions.OfferNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

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

    @ExceptionHandler(InvalidOfferException.class)
    public ResponseEntity<ErrorResponse> invalidOfferException(final Exception exception) {
        return createMessage(exception, UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<ErrorResponse> createMessage(final Exception exception, final HttpStatus httpStatus) {
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(httpStatus);
        errorResponse.setCode(httpStatus.value());
        errorResponse.setMessage(exception.getMessage()!= null && exception.getMessage().isEmpty()
                ? ERROR_NOT_DEFINITION : exception.getMessage());
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

}