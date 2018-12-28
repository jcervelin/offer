package io.jcervelin.ideas.offer.usecases;

import io.jcervelin.ideas.offer.gateways.repositories.OfferRepository;
import io.jcervelin.ideas.offer.models.Offer;
import io.jcervelin.ideas.offer.models.exceptions.OfferErrorException;
import io.jcervelin.ideas.offer.models.exceptions.OfferNotFoundException;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static br.com.six2six.fixturefactory.Fixture.from;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static io.jcervelin.ideas.offer.templates.OfferTemplate.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OfferManagementTest {

    private static String TEMPLATE_PACKAGE = "io.jcervelin.ideas.offer.templates";

    @BeforeClass
    public static void setup() {
        loadTemplates(TEMPLATE_PACKAGE);
    }

    @InjectMocks
    private OfferManagement target;

    @Mock
    private OfferRepository offerRepository;

    @Captor
    private ArgumentCaptor<ObjectId> objectIdCaptor;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void saveShouldReturnOfferErrorException() {

        doThrow(new RuntimeException("Mongo is outage.")).when(offerRepository).save(any(Offer.class));

        thrown.expect(OfferErrorException.class);
        thrown.expectMessage("The offer could not be saved. [Mongo is outage.]");

        target.save(new Offer());
    }

    @Test
    public void savePersistDataAndDoNotAlterTheContent() {
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70);

        doReturn(ivoryPiano).when(offerRepository).save(any(Offer.class));

        final Offer result = target.save(ivoryPiano);

        Assertions.assertThat(ivoryPiano).isEqualToIgnoringGivenFields(result,"id");
    }

    @Test
    public void getOffersShouldReturnOfferErrorException() {

        doThrow(new RuntimeException("Mongo is outage.")).when(offerRepository).findValidOffers(any(LocalDate.class));

        thrown.expect(OfferErrorException.class);
        thrown.expectMessage("The offer could not be found. [Mongo is outage.]");

        target.getValidOffers();
    }

    @Test
    public void getOffersShouldReturnNoDataFoundException() {

        doReturn(Collections.emptyList()).when(offerRepository).findValidOffers(any(LocalDate.class));

        thrown.expect(OfferNotFoundException.class);
        thrown.expectMessage("No data found.");

        target.getValidOffers();
    }

    @Test
    public void getOffersShouldNotAlterContentAndReturnWhateverDatabaseBrings() {

        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_EXPIRED);

        final Offer cabinet = from(Offer.class).gimme(WOODEN_CABINET_FROM_60_TO_40);

        doReturn(Arrays.asList(ivoryPiano,cabinet)).when(offerRepository).findValidOffers(any(LocalDate.class));

        final List<Offer> result = target.getValidOffers();

        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result).containsExactlyInAnyOrder(ivoryPiano,cabinet);
    }

    @Test
    public void cancelOfferShouldReturnCancelledOffer() {

        // GIVEN an expired order
        final Offer ivoryPianoExpired = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_EXPIRED);
        final String id = "5c2606d62be9ac82d9a1c119";

        doReturn(Optional.of(ivoryPianoExpired)).when(offerRepository).cancelOfferById(objectIdCaptor.capture());

        // WHEN the method cancelOffer is called it should return a cancelled order
        final Offer result = target.cancelOffer(id);

        // THEN the ObjectId created should be the same
        Assertions.assertThat(objectIdCaptor.getValue().toString()).isEqualTo(id);

        // AND the expired date should not be altered by this method.
        Assertions.assertThat(result.getEndOffer()).isEqualTo(ivoryPianoExpired.getEndOffer());
    }

    @Test
    public void cancelOfferShouldReturnOfferErrorExceptionWhenMongoIsOutage() {
        // GIVEN an random id
        final String id = "5c2606d62be9ac82d9a1c119";

        doThrow(new RuntimeException("Mongo is outage."))
                .when(offerRepository).cancelOfferById(eq(new ObjectId(id)));

        thrown.expect(OfferErrorException.class);
        thrown.expectMessage("The offer could not be cancelled. [Mongo is outage.]");

        // WHEN the method cancelOffer is called it should return a cancelled order
        target.cancelOffer(id);

        // THEN it should return OfferErrorException
    }

    @Test
    public void cancelOfferShouldReturnOfferNotFoundWhenIdIsNotFound() {
        // GIVEN an random id
        final String id = "5c2606d62be9ac82d9a1c119";

        doReturn(Optional.empty())
                .when(offerRepository).cancelOfferById(eq(new ObjectId(id)));

        thrown.expect(OfferNotFoundException.class);
        thrown.expectMessage("No data found.");

        // WHEN the method cancelOffer is called it should return a cancelled order
        target.cancelOffer(id);

        // THEN it should return OfferNotFoundException
    }

}