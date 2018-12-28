package io.jcervelin.ideas.offer.gateways.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jcervelin.ideas.offer.OfferApplication;
import io.jcervelin.ideas.offer.models.Offer;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static br.com.six2six.fixturefactory.Fixture.from;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static io.jcervelin.ideas.offer.templates.OfferTemplate.IVORY_PIANO_FROM_100_TO_70_VALID;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {OfferApplication.class}, webEnvironment = RANDOM_PORT)
@ComponentScan(basePackages = {"io.jcervelin.ideas.offer"})
public class OfferControllerTest {

    private static String TEMPLATE_PACKAGE = "io.jcervelin.ideas.offer.templates";

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
    public void getValidOffersShouldReturn1Item() throws Exception {
        final Offer ivoryPiano = from(Offer.class).gimme(IVORY_PIANO_FROM_100_TO_70_VALID);

        mongoTemplate.save(ivoryPiano);

        // WHEN
        final MvcResult mvcResult = mockMvc.perform(get("/offers").characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        // THEN
        final String content = new String(mvcResult
                .getResponse().getContentAsByteArray());

        List<Offer> results = objectMapper.readValue(content, new TypeReference<List<Offer>>() {});

        Assertions.assertThat(results.size()).isEqualTo(1);
        Assertions.assertThat(results).containsExactly(ivoryPiano);
    }
}