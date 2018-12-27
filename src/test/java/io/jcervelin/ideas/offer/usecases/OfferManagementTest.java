package io.jcervelin.ideas.offer.usecases;

import io.jcervelin.ideas.offer.gateways.repositories.OfferRepository;
import io.jcervelin.ideas.offer.models.Offer;
import io.jcervelin.ideas.offer.models.OfferErrorException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OfferManagementTest {

    @InjectMocks
    private OfferManagement target;

    @Mock
    private OfferRepository offerRepository;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void saveShouldReturnOfferErrorException() {

        doThrow(new RuntimeException("Mongo is outage.")).when(offerRepository).save(any(Offer.class));

        thrown.expect(OfferErrorException.class);
        thrown.expectMessage("The offer could not be saved. [Mongo is outage.]");

        target.save(new Offer());
    }


}