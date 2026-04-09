package com.ecalero.order.config;

import com.ecalero.order.util.AppProperties;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;

@Configuration
@RequiredArgsConstructor
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    private final AppProperties appProperties;

    @Override
    protected String getDatabaseName() {
        return appProperties.getMongoDatabase();
    }

    @Override
    @Bean
    public MongoClient reactiveMongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(appProperties.getMongoUri()))
                .build();
        return MongoClients.create(settings);
    }

    @Override
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new OffsetDateTimeWriteConverter(),
                new OffsetDateTimeReadConverter()
        ));
    }

    @WritingConverter
    static class OffsetDateTimeWriteConverter implements Converter<OffsetDateTime, Date> {
        @Override
        public Date convert(OffsetDateTime source) {
            return Date.from(source.toInstant());
        }
    }

    @ReadingConverter
    static class OffsetDateTimeReadConverter implements Converter<Date, OffsetDateTime> {
        @Override
        public OffsetDateTime convert(Date source) {
            return source.toInstant().atOffset(ZoneOffset.UTC);
        }
    }
}
