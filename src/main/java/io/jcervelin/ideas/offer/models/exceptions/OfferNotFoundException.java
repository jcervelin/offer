package io.jcervelin.ideas.offer.models.exceptions;

public class OfferNotFoundException extends RuntimeException {
    public OfferNotFoundException(final String message) {
        super (message);
    }
}
