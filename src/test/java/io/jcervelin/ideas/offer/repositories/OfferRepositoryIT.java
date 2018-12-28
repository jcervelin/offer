package io.jcervelin.ideas.offer.repositories;

import io.jcervelin.ideas.offer.gateways.repositories.OfferRepository;
import io.jcervelin.ideas.offer.models.Offer;
import io.jcervelin.ideas.offer.models.exceptions.OfferNotFoundException;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.six2six.fixturefactory.Fixture.from;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static io.jcervelin.ideas.offer.templates.OfferTemplate.*;
import static java.time.LocalDate.now;

@RunWith(SpringJUnit4ClassRunner.class)
@DataMongoTest
@ComponentScan(basePackages = {"io.jcervelin.ideas.offer"})
public class OfferRepositoryIT {

    private static String TEMPLATE_PACKAGE = "io.jcervelin.ideas.offer.templates";

    @BeforeClass
    public static void setup() {
        loadTemplates(TEMPLATE_PACKAGE);
    }

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
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70);
        final Offer ivoryPianoRepeatedItem = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70);

        target.save(ivoryPiano);
        target.save(ivoryPianoRepeatedItem);

        final List<Offer> all =
                mongoTemplate.findAll(Offer.class);

        Assertions.assertThat(all.size()).isEqualTo(2);

        Assertions.assertThat(all.get(0).getId()).isNotEqualTo(all.get(1).getId());

    }

    @Test
    public void getOffersShouldReturnOnlyNonExpiredOffers() {
        // GIVEN 2 pianos, one expired and another valid, saved in the database
        final Offer ivoryPianoValid = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_VALID);
        final Offer ivoryPianoExpired = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_EXPIRED);

        // WHEN the method getOffers is called only the valid offer should return
        target.save(ivoryPianoValid);
        target.save(ivoryPianoExpired);

        final List<Offer> result = target.findValidOffers(now());

        Assertions.assertThat(result.size()).isEqualTo(1);

        Assertions.assertThat(result.get(0)).isEqualToIgnoringGivenFields(ivoryPianoValid,"id");

    }

    @Test
    public void getOffersShouldReturnEmptyWhenAllOffersAreExpired() {
        // GIVEN 2 pianos, one expired and another valid, saved in the database
        final Offer ivoryPianoExpiredTwoDaysAgo = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_80_EXPIRED_TWO_DAYS);
        final Offer ivoryPianoExpiredOneDayAgo = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_EXPIRED);

        target.save(ivoryPianoExpiredTwoDaysAgo);
        target.save(ivoryPianoExpiredOneDayAgo);

        // WHEN the method getOffers is called the return should be empty, because there is no valid offer
        final List<Offer> result = target.findValidOffers(now());

        Assertions.assertThat(result.size()).isEqualTo(0);

    }

    @Test
    public void getOffersShouldReturnEmptyBecauseThereIsNoData() {
        // GIVEN a empty database

        // WHEN the method getOffers is called the return should be empty, because there is no valid offer
        final List<Offer> result = target.findValidOffers(now());

        Assertions.assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void cancelOfferShouldWork() {
        // GIVEN a valid offer, it should be cancelled
        final Offer ivoryPianoValid = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_VALID);
        final LocalDate now = LocalDate.now();
        target.save(ivoryPianoValid);
        final Offer offerWithId = target.findValidOffers(now).stream().findAny().orElseThrow(() -> new OfferNotFoundException("No data found."));

        // WHEN the method cancelOffer is called the return should
        // be the same offer with the attribute endOffer = today - 1 day

    }

    @Test(expected = RuntimeException.class)
    public void cancelOfferShouldReturnIllegalArgumentExceptionWhenNullIdIsGiven() {
        // GIVEN a valid offer, it should be cancelled
        final Optional<Offer> offer = target.cancelOfferById(null);

        // WHEN the method cancelOffer is called with a null id the return should
        // some kind of RuntimeException

    }

    @Test
    public void cancelOfferShouldReturnEmptyWhenIdIsNotFound() {
        // GIVEN a valid offer, it should be cancelled
        final Offer ivoryPianoValid = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_VALID);
        ivoryPianoValid.setId(new ObjectId());
        final Optional<Offer> offer = target.cancelOfferById(ivoryPianoValid.getId());

        // WHEN the method cancelOffer is called the return should
        // return exception OfferNotFoundException

    }


}