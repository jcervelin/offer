package io.jcervelin.ideas.offer.gateways.repositories;

import io.jcervelin.ideas.offer.models.Offer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface OfferMongoRepository extends MongoRepository<Offer, String> {

    @Query(value = "{'startOffer':{ $lt: ?0},'endOffer':{ $gte: ?0}}")
    List<Offer> findValidOffers(final LocalDate localDate);
}
