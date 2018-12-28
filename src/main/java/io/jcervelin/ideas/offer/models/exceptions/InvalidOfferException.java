package io.jcervelin.ideas.offer.models.exceptions;

public class InvalidOfferException extends RuntimeException{
    public InvalidOfferException(final String message) {
        super (message);
    }

}
