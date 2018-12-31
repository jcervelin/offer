package io.jcervelin.ideas.offer.configs.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;


/**
 * Class responsible for customise the date format of the JSON responses
 * the pattern to LocalDate is dd/MM/yyyy
 */
@Configuration
public class JacksonParser {

    @Bean
    public JavaTimeModule javaTimeModule() {
        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(ISO_DATE_TIME));
        return javaTimeModule;
    }

    @Bean
    @Primary
    public ObjectMapper jsonObjectMapper(final JavaTimeModule javaTimeModule) {
        return Jackson2ObjectMapperBuilder
                .json()
                .serializationInclusion(NON_NULL)
                .featuresToDisable(WRITE_DATES_AS_TIMESTAMPS)
                .serializerByType(ObjectId.class, new ToStringSerializer())
                .modules(javaTimeModule)
                .simpleDateFormat("dd/MM/yyyy'T'HH:mm:ss'Z'")
                .build();
    }

}