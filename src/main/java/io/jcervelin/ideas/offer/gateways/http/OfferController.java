package io.jcervelin.ideas.offer.gateways.http;

import io.jcervelin.ideas.offer.models.Offer;
import io.jcervelin.ideas.offer.usecases.OfferManagement;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for provide the http endpoints to
 * List valid offers
 * Save offers
 * Cancel offers
 *
 * Also it's possible to access using swagger.
 */
@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@Api(value = "/api/offers", description = "Offers Management")
public class OfferController {

    private final OfferManagement offerManagement;

    /**
     * This method is responsible for provide the list of the
     * valid offers. (Current offers and future ones)
     * @return all offers which the endOffer date is greater
     * or equals the current date.
     */
    @GetMapping
    @ApiOperation("Get list of the non expired offers")
    public ResponseEntity<List<Offer>> getValidOffers() {
        return new ResponseEntity<>(offerManagement.getValidOffers(), HttpStatus.OK);
    }

    /**
     * This method is responsible for provide save a valid
     * offer. It's considered a valid offer if it has a name
     * and startOffer date.
     * @return the offer saved with it's new id created automatically.
     */
    @PostMapping
    @ApiOperation("Save an offer. The name and the startOffer date are required. Pattern for dates dd/MM/yyyy")
    public ResponseEntity<Offer> saveOffer(@RequestBody final Offer offer) {
        return new ResponseEntity<>(offerManagement.save(offer), HttpStatus.OK);
    }

    /**
     * This method is responsible for provide cancel an offer.
     * The offer is not deleted from the database. It's only expired.
     * The endOffer date is replaced by yesterday. That's why the http
     * method is put instead of delete.
     * @return the offer saved with it's new id created automatically.
     */
    // If required, in the future it can be replaced by delete
    // or a job must be created to erase the old data and save disc.
    @PutMapping
    @ApiOperation("Cancel a valid offer by id.")
    public ResponseEntity<Offer> cancelOffer(@RequestBody final String id) {
        return new ResponseEntity<>(offerManagement.cancelOffer(id), HttpStatus.OK);
    }
}
