package io.jcervelin.ideas.offer.usecases;

import io.jcervelin.ideas.offer.gateways.repositories.OfferRepository;
import io.jcervelin.ideas.offer.models.Offer;
import io.jcervelin.ideas.offer.models.exceptions.OfferErrorException;
import io.jcervelin.ideas.offer.models.exceptions.OfferNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.time.LocalDate.now;

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

    /**
     * Method responsible for get the valid offers excluding the expired ones.
     * In case of database outage it should return OfferErrorException.
     * In case of data not found it should return OfferNotFoundException.
     *
     * @return list of valid offers
     */
    public List<Offer> getValidOffers() {
        try {
            final List<Offer> validOffers = repository.findValidOffers(now());
            if(validOffers.isEmpty())
                throw new OfferNotFoundException("No data found.");
            return validOffers;
        } catch (OfferNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new OfferErrorException(String.format("The offer could not be found. [%s]", e.getMessage()));
        }
    }

}
