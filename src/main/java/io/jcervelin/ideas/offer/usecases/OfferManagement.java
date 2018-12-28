package io.jcervelin.ideas.offer.usecases;

import io.jcervelin.ideas.offer.gateways.repositories.OfferRepository;
import io.jcervelin.ideas.offer.models.Offer;
import io.jcervelin.ideas.offer.models.exceptions.InvalidOfferException;
import io.jcervelin.ideas.offer.models.exceptions.OfferErrorException;
import io.jcervelin.ideas.offer.models.exceptions.OfferNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;

@RequiredArgsConstructor
@Component
public class OfferManagement {

    private final OfferRepository repository;

    private final Validator validator;

    /**
     * Method responsible for saving offers and wrap connection exceptions
     * or any kind of issue from Mongo in a customized exception class.
     *
     * @param offer
     * @return offer saved
     */
    public Offer save(final Offer offer) {
        try {
            final Set<ConstraintViolation<Offer>> validations = validator.validate(offer);
            if(validations.isEmpty())
                return repository.save(offer);
            else throw new InvalidOfferException(
                    validations
                            .stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining(" - ")));
        } catch (InvalidOfferException e) {
            throw e;
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

    /**
     * Method responsible for cancel Offer and return the proper kind of exception.
     * for business exceptions is OfferNotFoundException and technical exceptions is OfferErrorException.
     * @param id
     * @return Returns the offer canceled with the endDate = yesterday.
     */
    public Offer cancelOffer (final String id) {
        try {
            final Optional<Offer> offerCanceled = repository.cancelOfferById(new ObjectId(id));
            return offerCanceled.orElseThrow(() -> new OfferNotFoundException("No data found."));
        } catch (OfferNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new OfferErrorException(String.format("The offer could not be cancelled. [%s]", e.getMessage()));
        }
    }

}
