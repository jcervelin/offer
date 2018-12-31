package io.jcervelin.ideas.offer.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    private String id;
    @NotEmpty(message="The name is required")
    private String name;
    private double price;
    private double offerPrice;
    @NotNull(message="The startOffer is required")
    private LocalDate startOffer;
    private LocalDate endOffer;
    private String currency;
    private String description;
}
