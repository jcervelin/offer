package io.jcervelin.ideas.offer.repositories;

import io.jcervelin.ideas.offer.models.Offer;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OfferRepository extends MongoRepository<Offer, ObjectId> {
}
