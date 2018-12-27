package io.jcervelin.ideas.offer.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document
public class Offer {
    @Id
    private ObjectId sku;
    private String name;
    private double price;
    private double offerPrice;
    private LocalDate startOffer;
    private LocalDate endOffer;
}
