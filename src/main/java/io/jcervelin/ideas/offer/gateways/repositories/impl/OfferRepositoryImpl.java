package io.jcervelin.ideas.offer.gateways.repositories.impl;

import com.mongodb.client.result.DeleteResult;
import io.jcervelin.ideas.offer.gateways.repositories.OfferMongoRepository;
import io.jcervelin.ideas.offer.gateways.repositories.OfferRepository;
import io.jcervelin.ideas.offer.models.Offer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OfferRepositoryImpl implements OfferRepository {

    private final OfferMongoRepository repository;
    private final MongoTemplate mongoTemplate;

    @Override
    public List<Offer> findValidOffers (final LocalDate localDate) {
        return repository.findValidOffers(localDate);
    }

    @Override
    public List<Offer> findAll() {
        return repository.findAll();
    }

    /**
     * Method responsible for cancel Offer removing the offer from the database
     * @param id
     */
    @Override
    public boolean cancelOfferById (final String id) {
        DeleteResult deleteResult = mongoTemplate.remove(new Query().addCriteria(Criteria.where("_id").is(id)), Offer.class);
        return deleteResult.getDeletedCount() > 0;
    }

    @Override
    public Offer save(Offer offer) {
        return repository.save(offer);
    }


}
