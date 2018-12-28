package io.jcervelin.ideas.offer.gateways.repositories;

import io.jcervelin.ideas.offer.models.Offer;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OfferRepository {
    List<Offer> findValidOffers (final LocalDate localDate);
    Optional<Offer> cancelOfferById (final ObjectId id);
    Offer save(final Offer offer);
}
