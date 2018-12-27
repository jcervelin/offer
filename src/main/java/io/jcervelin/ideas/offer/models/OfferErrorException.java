package io.jcervelin.ideas.offer.models;

public class OfferErrorException extends RuntimeException{
    public OfferErrorException(final String message) {
        super (message);
    }
}