package io.jcervelin.ideas.offer.usecases;

import io.jcervelin.ideas.offer.gateways.repositories.OfferRepository;
import io.jcervelin.ideas.offer.models.Offer;
import io.jcervelin.ideas.offer.models.exceptions.OfferErrorException;
import io.jcervelin.ideas.offer.models.exceptions.OfferNotFoundException;
import io.jcervelin.ideas.offer.utils.OfferValidator;
import org.assertj.core.api.Assertions;
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

import static br.com.six2six.fixturefactory.Fixture.from;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static io.jcervelin.ideas.offer.templates.OfferTemplate.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OfferManagementTest {

    private static String TEMPLATE_PACKAGE = "io.jcervelin.ideas.offer.templates";

    public static final String MOCK_ID = "5c2606d62be9ac82d9a1c119";
    @BeforeClass
    public static void setup() {
        loadTemplates(TEMPLATE_PACKAGE);
    }

    @InjectMocks
    private OfferManagement target;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private OfferValidator offerValidator;

    @Captor
    private ArgumentCaptor<String> objectIdCaptor;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void saveShouldReturnOfferErrorException() {
        // GIVEN a RuntimeException
        doThrow(new RuntimeException("Mongo is outage.")).when(offerRepository).save(any(Offer.class));

        thrown.expect(OfferErrorException.class);
        thrown.expectMessage("The offer could not be saved. [Mongo is outage.]");

        // WHEN the method save is called
        target.save(new Offer());

        // THEN it should return OfferErrorException
    }

    @Test
    public void savePersistDataAndDoNotAlterTheContent() {
        // GIVEN an offer
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70);
        final Offer ivoryPianoWithId = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70);
        ivoryPianoWithId.setId(MOCK_ID);
        doReturn(ivoryPianoWithId).when(offerRepository).save(any(Offer.class));

        // WHEN the method is called
        final Offer result = target.save(ivoryPiano);

        // THEN it should the saved offer with an id
        Assertions.assertThat(result).isEqualToIgnoringGivenFields(ivoryPiano,"id");
        Assertions.assertThat(result.getId()).isNotNull();
    }

    @Test
    public void getValidOffersShouldReturnOfferErrorException() {
        // GIVEN a problem in the database
        doThrow(new RuntimeException("Mongo is outage.")).when(offerRepository).findValidOffers(any(LocalDate.class));

        thrown.expect(OfferErrorException.class);
        thrown.expectMessage("The offer could not be found. [Mongo is outage.]");

        // WHEN the method is called
        target.getValidOffers();

        // THEN it should return OfferErrorException
    }

    @Test
    public void getValidOffersShouldReturnNoDataFoundException() {
        // GIVEN an empty database
        doReturn(Collections.emptyList()).when(offerRepository).findValidOffers(any(LocalDate.class));

        thrown.expect(OfferNotFoundException.class);
        thrown.expectMessage("No data found.");

        //WHEN the method is called
        target.getValidOffers();

        // THEN it should return NoDataFoundException
    }

    @Test
    public void getValidOffersShouldNotAlterContentAndReturnWhateverDatabaseBrings() {
        // GIVEN two offer. One expired and the other valid
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_EXPIRED);
        final Offer cabinet = from(Offer.class).gimme(WOODEN_CABINET_FROM_60_TO_40);

        doReturn(Arrays.asList(ivoryPiano,cabinet)).when(offerRepository).findValidOffers(any(LocalDate.class));

        // WHEN the method is called
        final List<Offer> result = target.getValidOffers();

        // THEN it should return everything, because it's not this method responsibility
        // filtering expired and non expired methods
        // this is the database responsibility.
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result).containsExactlyInAnyOrder(ivoryPiano,cabinet);
    }

    @Test
    public void cancelOfferShouldReturnCancelledOffer() {

        // GIVEN an expired order
        doReturn(true).when(offerRepository).cancelOfferById(objectIdCaptor.capture());

        // WHEN the method cancelOffer is called it should return a cancelled order
        target.cancelOffer(MOCK_ID);

        // THEN the ObjectId created should be the same
        Assertions.assertThat(objectIdCaptor.getValue()).isEqualTo(MOCK_ID);

        verify(offerRepository,only()).cancelOfferById(MOCK_ID);
    }

    @Test
    public void cancelOfferShouldReturnOfferErrorExceptionWhenMongoIsOutage() {
        // GIVEN an random id
        final String id = MOCK_ID;

        doThrow(new RuntimeException("Mongo is outage."))
                .when(offerRepository).cancelOfferById(id);

        thrown.expect(OfferErrorException.class);
        thrown.expectMessage("The offer could not be cancelled. [Mongo is outage.]");

        // WHEN the method cancelOffer is called it should return a cancelled order
        target.cancelOffer(id);

        // THEN it should return OfferErrorException
    }

    @Test
    public void cancelOfferShouldReturnOfferNotFoundWhenIdIsNotFound() {
        // GIVEN an random id

        doReturn(false)
                .when(offerRepository).cancelOfferById(MOCK_ID);

        thrown.expect(OfferNotFoundException.class);
        thrown.expectMessage("No data found.");

        // WHEN the method cancelOffer is called it should return a cancelled order
        target.cancelOffer(MOCK_ID);

        // THEN it should return OfferNotFoundException
    }

    @Test
    public void getOffersShouldReturnOfferErrorException() {
        // GIVEN a problem in the database
        doThrow(new RuntimeException("Mongo is outage.")).when(offerRepository).findAll();

        thrown.expect(OfferErrorException.class);
        thrown.expectMessage("The offer could not be found. [Mongo is outage.]");

        // WHEN the method is called
        target.getOffers();

        // THEN it should return OfferErrorException
    }

    @Test
    public void getOffersShouldReturnNoDataFoundException() {
        // GIVEN an empty database
        doReturn(Collections.emptyList()).when(offerRepository).findAll();

        thrown.expect(OfferNotFoundException.class);
        thrown.expectMessage("No data found.");

        //WHEN the method is called
        target.getOffers();

        // THEN it should return NoDataFoundException
    }

    @Test
    public void getOffersShouldNotAlterContentAndReturnWhateverDatabaseBrings() {
        // GIVEN two offer. One expired and the other valid
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_EXPIRED);
        final Offer cabinet = from(Offer.class).gimme(WOODEN_CABINET_FROM_60_TO_40);

        doReturn(Arrays.asList(ivoryPiano,cabinet)).when(offerRepository).findAll();

        // WHEN the method is called
        final List<Offer> result = target.getOffers();

        // THEN it should return all offers
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result).containsExactlyInAnyOrder(ivoryPiano,cabinet);
    }

}