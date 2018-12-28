package io.jcervelin.ideas.offer.utils;

import io.jcervelin.ideas.offer.models.Offer;
import io.jcervelin.ideas.offer.models.exceptions.InvalidOfferException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class OfferValidator {
    private final Validator validator;

    public void validate(final Offer offer) {
        final Set<ConstraintViolation<Offer>> validations = validator.validate(offer);
        if(!validations.isEmpty())
            throw new InvalidOfferException(
                validations
                        .stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(" - ")));
    }
}
