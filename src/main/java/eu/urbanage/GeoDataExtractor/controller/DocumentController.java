package eu.urbanage.GeoDataExtractor.controller;

import eu.urbanage.GeoDataExtractor.keycloak.connector.KeycloakConnector;
import eu.urbanage.GeoDataExtractor.keycloak.model.KeycloakUser;
import eu.urbanage.GeoDataExtractor.model.Document;
import eu.urbanage.GeoDataExtractor.service.CronService;
import eu.urbanage.GeoDataExtractor.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentService ds;

    @Autowired
    private CronService cs;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private KeycloakConnector keycloakConnector;

    @PostMapping("/save/")
    public String postDocument(@RequestBody Document docJson) {

        LOGGER.info("Received document store: " + docJson.getName());

        KeycloakUser userInfo = keycloakConnector.getUserFromToken(getAuthToken());

        docJson.setUserID(userInfo.getSub());

        docJson.setUserEmail(userInfo.getEmail());

        docJson.setOnIDRA(false);

        docJson.setDateCreation(new Date());

        ds.addDocument(docJson);

        return docJson.getId();

    }

    @PostMapping("/update/")
    public ResponseEntity<Document> updateDocument(@RequestBody Document docJson) {

        LOGGER.info("Received document update: " + docJson.getName());

        KeycloakUser userInfo = keycloakConnector.getUserFromToken(getAuthToken());

        docJson.setUserID(userInfo.getSub());

        docJson.setUserEmail(userInfo.getEmail());

        return ds.updateDocument(docJson);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable() String id) {

        try {

            return ds.findDocument(id);

        } catch (Exception e) {
            LOGGER.error(id, e);
            return ResponseEntity.internalServerError().body(null);
        }

    }

    @GetMapping("/getdocuments")
    public ResponseEntity<List<Document>> getAllDocument() {

        try {

            KeycloakUser userInfo = keycloakConnector.getUserFromToken(getAuthToken());

            return ds.findAllDocumentOfUser(userInfo.getSub());

        } catch (Exception e) {
            LOGGER.error(String.valueOf(e));

            return ResponseEntity.internalServerError().body(null);

        }

    }

    @GetMapping("/getalldocuments/{city}")
    public ResponseEntity<List<Document>> getAllDocumentByCity(@PathVariable() String city) {

        try {

            return ds.findAllDocumentByCity(city);

        } catch (Exception e) {
            LOGGER.error(String.valueOf(e));
            return ResponseEntity.internalServerError().body(null);
        }

    }

    @GetMapping("/getGeojson/{id}")
    public ResponseEntity<Object> getGeojsonByID(@PathVariable() String id) {

        try {

            return ds.findDocumentGeojson(id);

        } catch (Exception e) {
            LOGGER.error(String.valueOf(e));
            return ResponseEntity.internalServerError().body(null);
        }

    }

    @GetMapping("/getalldocuments")
    public ResponseEntity<List<Document>> getAllDocuments() {

        try {

            return ds.findAllDocument();

        } catch (Exception e) {
            LOGGER.error(String.valueOf(e));
            return ResponseEntity.internalServerError().body(null);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable() String id) {

        try {

            Document releated_document = ds.findDocumentObj(id);

            cs.deleteCron(releated_document.getCron_id());

            return ds.deleteDocument(id);

        } catch (Exception e) {
            LOGGER.error(id, e);
            return ResponseEntity.internalServerError().body(null);
        }

    }

    private String getAuthToken() {

        return request.getHeader("Authorization");

    }

}
