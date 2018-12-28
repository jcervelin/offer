package io.jcervelin.ideas.offer.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "offers")
public class Offer implements Serializable {

    private static final long serialVersionUID = 5184262962570378015L;

    @Id
    private ObjectId id;
    private String name;
    private double price;
    private double offerPrice;
    private LocalDate startOffer;
    private LocalDate endOffer;
    private String currency;
    private String description;
}
