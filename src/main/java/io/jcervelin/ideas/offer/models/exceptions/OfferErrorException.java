package io.jcervelin.ideas.offer.models.exceptions;

public class OfferErrorException extends RuntimeException{
    public OfferErrorException(final String message) {
        super (message);
    }
}