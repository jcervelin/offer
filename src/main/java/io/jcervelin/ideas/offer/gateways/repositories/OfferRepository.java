package io.jcervelin.ideas.offer.gateways.repositories;

import io.jcervelin.ideas.offer.models.Offer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OfferRepository {
    List<Offer> findAll();
    List<Offer> findValidOffers (final LocalDate localDate);
    Optional<Offer> cancelOfferById (final String id);
    Offer save(final Offer offer);
}
