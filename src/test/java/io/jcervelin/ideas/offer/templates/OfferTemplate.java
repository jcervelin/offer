package io.jcervelin.ideas.offer.templates;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import io.jcervelin.ideas.offer.models.Offer;

import java.time.LocalDate;

public class OfferTemplate implements TemplateLoader {

    public static final String IVORY_PIANO_FROM_100_TO_70 = "Ivory Piano from 100 to 70";
    public static final String IVORY_PIANO_FROM_100_TO_70_VALID = "Ivory Piano from 100 to 70 VALID";
    public static final String IVORY_PIANO_FROM_100_TO_70_EXPIRED = "Ivory Piano from 100 to 70 EXPIRED";
    public static final String IVORY_PIANO_FROM_100_TO_80_EXPIRED_TWO_DAYS = "Ivory Piano from 100 to 80 EXPIRED TWO DAYS";
    public static final String WOODEN_CABINET_FROM_60_TO_40 = "Wooden Cabinet from 60 to 40";

    private LocalDate now = LocalDate.now();

    @Override
    public void load() {
        Fixture.of(Offer.class).addTemplate(IVORY_PIANO_FROM_100_TO_70, new Rule() {{
            add("name", "Ivory Piano from 100 to 70");
            add("price", 100.0);
            add("offerPrice", 70.0);
            add("startOffer", LocalDate.of(2018, 12, 1));
            add("endOffer", LocalDate.of(2018, 12, 10));
            add("currency", "GBP");
            add("description", "Amazing and fancy Ivory Piano from 100 to 70.");
        }});

        Fixture.of(Offer.class).addTemplate(IVORY_PIANO_FROM_100_TO_70_VALID).inherits(IVORY_PIANO_FROM_100_TO_70, new Rule(){{
            add("endOffer", now.plusDays(1));
        }});

        Fixture.of(Offer.class).addTemplate(IVORY_PIANO_FROM_100_TO_70_EXPIRED).inherits(IVORY_PIANO_FROM_100_TO_70, new Rule(){{
            add("endOffer", now.minusDays(1));
        }});

        Fixture.of(Offer.class).addTemplate(IVORY_PIANO_FROM_100_TO_80_EXPIRED_TWO_DAYS).inherits(IVORY_PIANO_FROM_100_TO_70, new Rule(){{
            add("endOffer", now.minusDays(2));
            add("offerPrice", 80.0);
        }});

        Fixture.of(Offer.class).addTemplate(WOODEN_CABINET_FROM_60_TO_40, new Rule() {{
            add("name", "Wooden Cabinet from 60 to 40");
            add("price", 60.0);
            add("offerPrice", 40.0);
            add("startOffer", LocalDate.of(2018, 12, 1));
            add("endOffer", LocalDate.of(2018, 12, 10));
            add("currency", "GBP");
            add("description", "Amazing and fancy Wooden Cabinet from 60 to 40.");
        }});
    }
}
