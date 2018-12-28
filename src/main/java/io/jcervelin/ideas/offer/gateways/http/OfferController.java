package io.jcervelin.ideas.offer.gateways.http;

import io.jcervelin.ideas.offer.models.Offer;
import io.jcervelin.ideas.offer.usecases.OfferManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Offer> saveOffer(@RequestBody final Offer offer) {
        return new ResponseEntity<>(offerManagement.save(offer), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Offer> cancelOffer(@RequestBody final String id) {
        return new ResponseEntity<>(offerManagement.cancelOffer(id), HttpStatus.OK);
    }
}
