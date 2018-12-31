package io.jcervelin.ideas.offer.gateways.repositories.impl;

import io.jcervelin.ideas.offer.gateways.repositories.OfferMongoRepository;
import io.jcervelin.ideas.offer.gateways.repositories.OfferRepository;
import io.jcervelin.ideas.offer.models.Offer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class OfferRepositoryImpl implements OfferRepository {

    private final OfferMongoRepository repository;
    private final MongoTemplate mongoTemplate;

    @Override
    public List<Offer> findValidOffers (final LocalDate localDate) {
        return repository.findValidOffers(localDate);
    }

    /**
     * Method responsible for cancel Offer updating the endOffer to LocalDate.now() - 1 day
     * @param id
     * @return when id is not found it returns an Optional.empty()
     */
    @Override
    public Optional<Offer> cancelOfferById (final String id) {

        mongoTemplate.updateFirst(
                new Query().addCriteria(Criteria.where("_id").is(id)),
                new Update().set("endOffer",LocalDate.now().minusDays(1)),
                Offer.class
        );

        return repository.findById(id);
    }

    @Override
    public Offer save(Offer offer) {
        return repository.save(offer);
    }


}
