package eu.urbanage.GeoDataExtractor.service;

import eu.urbanage.GeoDataExtractor.model.Document;
import eu.urbanage.GeoDataExtractor.repository.ConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    protected ConfigRepository dRepo;

    public ResponseEntity<Document> findDocument(String id) {

        try {

            Optional<Document> foundDocument = dRepo.findById(id);

            if (foundDocument.isPresent()) {
                return ResponseEntity.ok().body(foundDocument.get());
            }
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Error retrieving document with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Object> findDocumentGeojson(String id) {

        try {

            Optional<Document> foundDocument = dRepo.findById(id);

            if (foundDocument.isPresent()) {

                return ResponseEntity.ok().body(foundDocument.get().getGeojson());
            }
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Error retrieving geojson for document with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public Document findDocumentObj(String id) {

        try {

            Optional<Document> foundDocument = dRepo.findById(id);
            // Returns the document or null if not found. The caller must handle the null case.
            return foundDocument.orElse(null);
        } catch (Exception e) {
            log.error("Error finding document object with id {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    public ResponseEntity<List<Document>> findAllDocumentOfUser(String userID) {

        try {

            List<Document> foundDocuments = dRepo.findByUserID(userID);

            if (foundDocuments.isEmpty()) {

                List<Document> emptyList = Collections.emptyList();

                return ResponseEntity.ok(emptyList);
            }

            return ResponseEntity.ok().body(foundDocuments);

        } catch (Exception e) {
            log.error("Error retrieving documents for user {}: {}", userID, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Document>> findAllDocumentByCity(String city) {

        try {

            List<Document> foundDocuments = dRepo.findBycityName(city);

            return ResponseEntity.ok().body(foundDocuments);

        } catch (Exception e) {
            log.error("Error retrieving documents for city {}: {}", city, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Document>> findAllDocument() {

        try {

            List<Document> foundDocuments = dRepo.findAll();

            return ResponseEntity.ok().body(foundDocuments);

        } catch (Exception e) {
            log.error("Error retrieving all documents: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<String> deleteDocument(String id) {

        try {

            dRepo.deleteById(id);

            return ResponseEntity.noContent().build();

        } catch (EmptyResultDataAccessException e) {
            // This means the document with the given ID was not found.
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting document with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public Document addDocument(Document doc) {

        try {
            return dRepo.save(doc);

        } catch (Exception e) {
            log.error("Error saving document '{}': {}", doc.getName(), e.getMessage(), e);
            return null;
        }
    }

    public ResponseEntity<Document> updateDocument(Document doc) {

        String id = doc.getId();

        try {

            Optional<Document> foundDocumentOpt = dRepo.findById(id);

            if (foundDocumentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Document existingDocument = foundDocumentOpt.get();

            // Update fields from the request, but preserve immutable fields
            existingDocument.setName(doc.getName());
            existingDocument.setDescription(doc.getDescription());
            existingDocument.setCityName(doc.getCityName());
            existingDocument.setFilter(doc.getFilter());
            existingDocument.setSubfilter(doc.getSubfilter());
            existingDocument.setGeojson(doc.getGeojson());
            existingDocument.setLayers(doc.getLayers());
            existingDocument.setOnIDRA(doc.isOnIDRA());
            existingDocument.setUserID(doc.getUserID());
            existingDocument.setUserEmail(doc.getUserEmail());

            dRepo.save(existingDocument);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("Error updating document with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public void updateDocumentFromCron(Document doc) {

        // This method overwrites the document. For safety, it should check if the document exists.
        // This method assumes the document exists and overwrites it.
        // It's better to use the same logic as updateDocument for safety.
        dRepo.save(doc);

    }

}
