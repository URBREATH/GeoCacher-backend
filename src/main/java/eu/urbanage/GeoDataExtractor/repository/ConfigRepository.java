package eu.urbanage.GeoDataExtractor.repository;

import eu.urbanage.GeoDataExtractor.model.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigRepository extends MongoRepository<Document, String> {

    Optional<Document> findById(String id);

    void deleteById(String id);

    Document save(Document doc);

    List<Document> findByUserID(String userID);

    List<Document> findBycityName(String city);
}
