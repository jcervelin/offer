package io.jcervelin.ideas.offer.gateways.http;

import io.jcervelin.ideas.offer.models.Offer;
import io.jcervelin.ideas.offer.usecases.OfferManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferManagement offerManagement;

    @GetMapping
    public ResponseEntity<List<Offer>> getValidOffers() {
        return new ResponseEntity<>(offerManagement.getValidOffers(), HttpStatus.OK);
    }
}
