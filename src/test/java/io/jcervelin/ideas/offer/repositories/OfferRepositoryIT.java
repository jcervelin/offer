package io.jcervelin.ideas.offer.repositories;

import io.jcervelin.ideas.offer.gateways.repositories.OfferRepository;
import io.jcervelin.ideas.offer.models.Offer;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@DataMongoTest
public class OfferRepositoryIT {

    @Autowired
    private OfferRepository target;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        mongoTemplate
                .getCollectionNames()
                .forEach(mongoTemplate::dropCollection);

    }

    @Test
    public void saveShouldSaveRepeatedElementsWithDifferentSkus() {
        final Offer ivoryPiano = Offer.builder()
                .name("Ivory Piano")
                .price(100.0)
                .offerPrice(70.0)
                .startOffer(LocalDate.of(2018, 12, 1))
                .endOffer(LocalDate.of(2018, 12, 10))
                .build();

        final Offer ivoryPianoRepeatedItem = Offer.builder()
                .name("Ivory Piano")
                .price(100.0)
                .offerPrice(70.0)
                .startOffer(LocalDate.of(2018, 12, 1))
                .endOffer(LocalDate.of(2018, 12, 10))
                .build();

        target.save(ivoryPiano);
        target.save(ivoryPianoRepeatedItem);

        final List<Offer> all =
                target.findAll();

        Assertions.assertThat(all.size()).isEqualTo(2);

        Assertions.assertThat(all.get(0).getSku()).isNotEqualTo(all.get(1).getSku());

    }

}