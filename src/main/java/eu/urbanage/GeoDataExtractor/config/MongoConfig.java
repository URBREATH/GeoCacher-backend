package eu.urbanage.GeoDataExtractor.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableMongoRepositories(basePackages = "eu.urbanage.GeoDataExtractor.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    private String mongodbUrl;
    private String mongodbDatabase;

    MongoConfig(@Value("${MONGODB_URL}") String mongodbUrl, @Value("${PLATFORM}") String platform) {
        this.mongodbUrl = mongodbUrl;

        if ("dev".equals(platform)) {
            this.mongodbDatabase = "URBANAGE_DEV";
        } else if ("prod".equals(platform)) {
            this.mongodbDatabase = "URBANAGE_PROD";
        } else {
            this.mongodbDatabase = "URBANAGE";
        }
    }

    @Override
    protected String getDatabaseName() {
        return mongodbDatabase;
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongodbUrl + "/" + mongodbDatabase);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public Collection getMappingBasePackages() {
        return Collections.singleton("eu.urbanage.GeoDataExtractor.repository");
    }
}
