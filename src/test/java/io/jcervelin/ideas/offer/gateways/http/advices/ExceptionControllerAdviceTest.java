package io.jcervelin.ideas.offer.gateways.http.advices;

import io.jcervelin.ideas.offer.models.exceptions.ErrorResponse;
import io.jcervelin.ideas.offer.models.exceptions.OfferErrorException;
import io.jcervelin.ideas.offer.models.exceptions.OfferNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionControllerAdviceTest {

    @InjectMocks
    private ExceptionControllerAdvice target;

    @Test
    public void handleException() {
        ResponseEntity<ErrorResponse> errorResponseResponseEntity = target.handleException(new Exception());
        Assertions.assertThat(errorResponseResponseEntity.getStatusCodeValue()).isEqualTo(500);
    }

    @Test
    public void handleExceptionWithEmptyMessage() {
        ResponseEntity<ErrorResponse> errorResponseResponseEntity = target.handleException(new Exception(""));
        Assertions.assertThat(errorResponseResponseEntity.getStatusCodeValue()).isEqualTo(500);
    }

    @Test
    public void noContentFound() {
        ResponseEntity<ErrorResponse> errorResponseResponseEntity = target.noContentFound(new OfferNotFoundException("Data not found."));
        Assertions.assertThat(errorResponseResponseEntity.getStatusCodeValue()).isEqualTo(204);
    }

    @Test
    public void offerErrorException() {
        ResponseEntity<ErrorResponse> errorResponseResponseEntity = target.offerErrorException(new OfferErrorException("Mongo outage."));
        Assertions.assertThat(errorResponseResponseEntity.getStatusCodeValue()).isEqualTo(500);

    }

    @Test
    public void invalidOfferException() {
        ResponseEntity<ErrorResponse> errorResponseResponseEntity = target.invalidOfferException(new OfferErrorException("Name required."));
        Assertions.assertThat(errorResponseResponseEntity.getStatusCodeValue()).isEqualTo(422);

    }
}