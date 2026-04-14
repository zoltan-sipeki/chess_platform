package net.chess_platform.chat_service.config;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions.MongoConverterConfigurationAdapter;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.mongodb.database}")
    private String DATABASE;

    @Value("${spring.mongodb.host}")
    private String HOST;

    @Value("${spring.mongodb.port}")
    private int PORT;

    @Value("${spring.mongodb.username}")
    private String USERNAME;

    @Value("${spring.mongodb.password}")
    private String PASSWORD;

    @ReadingConverter
    public static class OffsetDateTimeReadConverter implements Converter<Date, OffsetDateTime> {

        @Override
        public OffsetDateTime convert(Date date) {
            return date.toInstant().atOffset(ZoneOffset.UTC);
        }
    }

    @WritingConverter
    public class OffsetDateTimeWriteConverter implements Converter<OffsetDateTime, Date> {

        @Override
        public Date convert(OffsetDateTime date) {
            return Date.from(date.toInstant());
        }
    }

    @Override
    protected String getDatabaseName() {
        return DATABASE;
    }

    @Override
    protected void configureConverters(MongoConverterConfigurationAdapter adapter) {
        adapter.registerConverter(new OffsetDateTimeReadConverter());
        adapter.registerConverter(new OffsetDateTimeWriteConverter());
    }

    @Bean
    public MongoClient mongoClient(MongoClientSettings settings) {
        return MongoClients.create(settings);
    }

    @Bean
    public MongoOperations mongoTemplate(MongoClient mongoClient) {
        var template = new MongoTemplate(mongoClient, getDatabaseName());
        return template;
    }

    @Bean
    public MongoClientSettings mongoClientSettings() {
        return MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://" + HOST + ":" + PORT + "/" + DATABASE))
                .credential(
                        MongoCredential.createCredential(USERNAME, getDatabaseName(), PASSWORD.toCharArray()))
                .uuidRepresentation(UuidRepresentation.STANDARD).build();
    }
}
