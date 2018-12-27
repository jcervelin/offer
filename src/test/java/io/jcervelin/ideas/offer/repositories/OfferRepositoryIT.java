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

import static java.time.LocalDate.now;

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

    @Test
    public void getOffersShouldReturnOnlyNonExpiredOffers() {
        // GIVEN 2 pianos, one expired and another valid, saved in the database

        final Offer ivoryPianoValid = Offer.builder()
                .name("Ivory Piano")
                .price(100.0)
                .offerPrice(70.0)
                .startOffer(LocalDate.of(2018, 12, 1))
                .endOffer(now().plusDays(1))
                .build();

        final Offer ivoryPianoExpired = Offer.builder()
                .name("Ivory Piano Expired")
                .price(100.0)
                .offerPrice(80.0)
                .startOffer(LocalDate.of(2018, 12, 1))
                .endOffer(now().minusDays(1))
                .build();

        // WHEN the method getOffers is called only the valid offer should return
        target.save(ivoryPianoValid);
        target.save(ivoryPianoExpired);

        List<Offer> result = target.findValidOffers(now());

        Assertions.assertThat(result.size()).isEqualTo(1);

        Assertions.assertThat(result.get(0)).isEqualToIgnoringGivenFields(ivoryPianoValid,"id");

    }

    @Test
    public void getOffersShouldReturnEmptyWhenAllOffersAreExpired() {
        // GIVEN 2 pianos, one expired and another valid, saved in the database

        final Offer ivoryPianoExpiredTwoDaysAgo = Offer.builder()
                .name("Ivory Piano")
                .price(100.0)
                .offerPrice(70.0)
                .startOffer(LocalDate.of(2018, 12, 1))
                .endOffer(now().minusDays(2))
                .build();

        final Offer ivoryPianoExpiredOneDayAgo = Offer.builder()
                .name("Ivory Piano")
                .price(100.0)
                .offerPrice(80.0)
                .startOffer(LocalDate.of(2018, 12, 1))
                .endOffer(now().minusDays(1))
                .build();

        // WHEN the method getOffers is called the return should be empty, because there is no valid offer

    }

    @Test
    public void getOffersShouldReturnEmptyBecauseThereIsNoData() {
        // GIVEN a empty database

        // WHEN the method getOffers is called the return should be empty, because there is no valid offer

    }


}