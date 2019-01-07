package io.jcervelin.ideas.offer.gateways.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jcervelin.ideas.offer.OfferApplication;
import io.jcervelin.ideas.offer.models.Offer;
import io.jcervelin.ideas.offer.models.exceptions.ErrorResponse;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static br.com.six2six.fixturefactory.Fixture.from;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static io.jcervelin.ideas.offer.templates.OfferTemplate.IVORY_PIANO_FROM_100_TO_70_VALID;
import static io.jcervelin.ideas.offer.templates.OfferTemplate.WOODEN_CABINET_FROM_60_TO_40;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {OfferApplication.class}, webEnvironment = RANDOM_PORT)
@ComponentScan(basePackages = {"io.jcervelin.ideas.offer"})
public class OfferControllerIT {

    private static String TEMPLATE_PACKAGE = "io.jcervelin.ideas.offer.templates";
    private static String ENDPOINT = "/api/offers";
    private static String ALL_OFFERS = ENDPOINT + "/all";

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    private MockMvc mockMvc;


    @BeforeClass
    public static void setup() {
        loadTemplates(TEMPLATE_PACKAGE);
    }

    @Before
    public void setUp() {
        mongoTemplate
                .getCollectionNames()
                .forEach(mongoTemplate::dropCollection);

        mockMvc = webAppContextSetup(webAppContext).build();
    }

    @Test
    public void getValidOffersShouldReturn1Offer() throws Exception {
        // GIVEN 1 valid offer saved
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_VALID);
        mongoTemplate.save(ivoryPiano);

        // WHEN the endpoint is called
        final MvcResult mvcResult = mockMvc.perform(get(ENDPOINT).characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        // THEN a status 200 and content should be returned
        final String content = new String(mvcResult
                .getResponse().getContentAsByteArray());

        final List<Offer> results = objectMapper.readValue(content, new TypeReference<List<Offer>>() {});

        Assertions.assertThat(results.size()).isEqualTo(1);
        Assertions.assertThat(results).containsExactly(ivoryPiano);
    }

    @Test
    public void getValidOffersShouldReturnOnlyValidOffers() throws Exception {
        // GIVEN 1 valid offer and 1 expired offer saved
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_VALID);
        mongoTemplate.save(ivoryPiano);

        final Offer cabinetExpired = from(Offer.class).gimme(WOODEN_CABINET_FROM_60_TO_40);
        mongoTemplate.save(cabinetExpired);

        // WHEN the endpoint is called
        final MvcResult mvcResult = mockMvc.perform(get(ENDPOINT).characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        // THEN a status 200 and a content with the valid offer should be returned
        final String content = new String(mvcResult
                .getResponse().getContentAsByteArray());

        final List<Offer> results = objectMapper.readValue(content, new TypeReference<List<Offer>>() {});

        Assertions.assertThat(results.size()).isEqualTo(1);
        Assertions.assertThat(results).containsExactly(ivoryPiano);
    }

    @Test
    public void getValidOffersShouldOfferNotFoundException() throws Exception {
        // GIVEN none offer

        // WHEN the endpoint is called
        final MvcResult mvcResult = mockMvc.perform(get(ENDPOINT).characterEncoding("utf-8"))
                .andExpect(status().isNoContent())
                .andReturn();

        // THEN a status 204 (NO CONTENT) should be returned
        final String content = new String(mvcResult
                .getResponse().getContentAsByteArray());

        ErrorResponse result = objectMapper.readValue(content,ErrorResponse.class);

        Assertions.assertThat(result.getCode()).isEqualTo(204);
        Assertions.assertThat(result.getMessage()).isEqualTo("No data found.");
        Assertions.assertThat(result.getStatus().getReasonPhrase()).isEqualTo("No Content");
    }

    @Test
    public void saveOffersShouldSaveAndGenerateANewId() throws Exception {
        // GIVEN 1 valid offer
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_VALID);

        // WHEN the endpoint is called
        final MvcResult mvcResult = mockMvc.perform(post(ENDPOINT)
                .content(objectMapper.writeValueAsBytes(ivoryPiano))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        // THEN a status 200 and a content with the valid offer should be returned
        final String content = new String(mvcResult
                .getResponse().getContentAsByteArray());

        final Offer result = objectMapper.readValue(content, Offer.class);

        Assertions.assertThat(result).isEqualToIgnoringGivenFields(ivoryPiano,"id");
        Assertions.assertThat(result.getId()).isNotNull();
    }

    @Test
    public void saveOffersShouldReturnErrorWhenEmptyContentIsSent() throws Exception {
        // GIVEN 1 valid offer
        final Offer ivoryPiano = null;

        // WHEN the endpoint is called
        final MvcResult mvcResult = mockMvc.perform(post(ENDPOINT)
                .content(objectMapper.writeValueAsString(ivoryPiano))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        // THEN a status 500 with error message should be returned
        final String content = new String(mvcResult
                .getResponse().getContentAsByteArray());

        final ErrorResponse result = objectMapper.readValue(content, ErrorResponse.class);
        Assertions.assertThat(result.getCode()).isEqualTo(500);
        Assertions.assertThat(result.getStatus().getReasonPhrase()).isEqualTo("Internal Server Error");
        Assertions.assertThat(result.getStatus().getReasonPhrase()).isEqualTo("Internal Server Error");
        Assertions.assertThat(result.getMessage()).isEqualTo("Required request body is missing: public org.springframework.http.ResponseEntity<io.jcervelin.ideas.offer.models.Offer> io.jcervelin.ideas.offer.gateways.http.OfferController.saveOffer(io.jcervelin.ideas.offer.models.Offer)");
    }

    @Test
    public void saveOffersShouldReturnErrorWhenThereIsNoName() throws Exception {
        // GIVEN 1 valid offer
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_VALID);
        ivoryPiano.setName(null);
        // WHEN the endpoint is called
        final MvcResult mvcResult = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ivoryPiano)))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        // THEN a status 422 with error message should be returned
        final String content = new String(mvcResult
                .getResponse().getContentAsByteArray());

        final ErrorResponse result = objectMapper.readValue(content, ErrorResponse.class);
        Assertions.assertThat(result.getCode()).isEqualTo(422);
        Assertions.assertThat(result.getMessage()).isEqualTo("The name is required");
        Assertions.assertThat(result.getStatus().getReasonPhrase()).isEqualTo("Unprocessable Entity");

    }

    @Test
    public void saveOffersShouldReturnErrorWhenThereIsNoStartOffer() throws Exception {
        // GIVEN 1 valid offer
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_VALID);
        ivoryPiano.setStartOffer(null);
        // WHEN the endpoint is called
        final MvcResult mvcResult = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ivoryPiano)))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        // THEN a status 422 with error message should be returned
        final String content = new String(mvcResult
                .getResponse().getContentAsByteArray());

        final ErrorResponse result = objectMapper.readValue(content, ErrorResponse.class);
        Assertions.assertThat(result.getCode()).isEqualTo(422);
        Assertions.assertThat(result.getMessage()).isEqualTo("The startOffer is required");
        Assertions.assertThat(result.getStatus().getReasonPhrase()).isEqualTo("Unprocessable Entity");
    }

    @Test
    public void saveOffersShouldReturnErrorWhenThereIsNoNameAndStartOffer() throws Exception {
        // GIVEN 1 valid offer
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_VALID);
        ivoryPiano.setStartOffer(null);
        ivoryPiano.setName("");
        // WHEN the endpoint is called
        final MvcResult mvcResult = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ivoryPiano)))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        // THEN a status 422 with error message should be returned
        final String content = new String(mvcResult
                .getResponse().getContentAsByteArray());

        final ErrorResponse result = objectMapper.readValue(content, ErrorResponse.class);
        Assertions.assertThat(result.getCode()).isEqualTo(422);
        Assertions.assertThat(result.getMessage())
                .contains("The startOffer is required")
                .contains("The name is required");
        Assertions.assertThat(result.getStatus().getReasonPhrase()).isEqualTo("Unprocessable Entity");
    }

    @Test
    public void cancelOffersShouldRemoveTheOffer() throws Exception {
        // GIVEN 1 valid offer saved
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_VALID);
        final Offer savedWithId = mongoTemplate.save(ivoryPiano);

        // WHEN the endpoint is called
        final MvcResult mvcResult = mockMvc.perform(delete(ENDPOINT + "/" + savedWithId.getId())
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        // THEN a status 200 and a content with the valid offer should be returned
        final String content = mvcResult
                .getResponse().getContentAsString();

        Assertions.assertThat(content).isEqualTo("Removed");
    }

    @Test
    public void getOffersShouldReturnAllOffers() throws Exception {
        // GIVEN 1 valid offer and 1 expired offer saved
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_VALID);
        mongoTemplate.save(ivoryPiano);

        final Offer cabinetExpired = from(Offer.class).gimme(WOODEN_CABINET_FROM_60_TO_40);
        mongoTemplate.save(cabinetExpired);

        // WHEN the endpoint is called
        final MvcResult mvcResult = mockMvc.perform(get(ALL_OFFERS).characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        // THEN a status 200 and a content with the valid offer should be returned
        final String content = new String(mvcResult
                .getResponse().getContentAsByteArray());

        final List<Offer> results = objectMapper.readValue(content, new TypeReference<List<Offer>>() {});

        Assertions.assertThat(results.size()).isEqualTo(2);
        Assertions.assertThat(results).containsExactly(ivoryPiano,cabinetExpired);
    }

    @Test
    public void getOffersShouldOfferNotFoundException() throws Exception {
        // GIVEN none offer

        // WHEN the endpoint is called
        final MvcResult mvcResult = mockMvc.perform(get(ALL_OFFERS).characterEncoding("utf-8"))
                .andExpect(status().isNoContent())
                .andReturn();

        // THEN a status 204 (NO CONTENT) should be returned
        final String content = new String(mvcResult
                .getResponse().getContentAsByteArray());

        ErrorResponse result = objectMapper.readValue(content,ErrorResponse.class);

        Assertions.assertThat(result.getCode()).isEqualTo(204);
        Assertions.assertThat(result.getMessage()).isEqualTo("No data found.");
        Assertions.assertThat(result.getStatus().getReasonPhrase()).isEqualTo("No Content");
    }

}