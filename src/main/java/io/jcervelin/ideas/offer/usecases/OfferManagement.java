package io.jcervelin.ideas.offer.usecases;

import io.jcervelin.ideas.offer.gateways.repositories.OfferRepository;
import io.jcervelin.ideas.offer.models.Offer;
import io.jcervelin.ideas.offer.models.OfferErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OfferManagement {

    private final OfferRepository repository;

    /**
     * Method responsible for saving offers and wrap connection exceptions or any kind of issue from Mongo in a
     * customized exception class.
     *
     * @param offer
     * @return offer saved
     */
    public Offer save(final Offer offer) {
        try {
            return repository.save(offer);
        } catch (Exception e) {
            throw new OfferErrorException(String.format("The offer could not be saved. [%s]", e.getMessage()));
        }
    }

}
