package io.jcervelin.ideas.offer.models;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startOffer;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endOffer;
    private String currency;
    private String description;
}
