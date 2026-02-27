package eu.urbanage.GeoDataExtractor.controller;

import com.fasterxml.jackson.databind.JsonNode;
import eu.urbanage.GeoDataExtractor.model.Document;
import eu.urbanage.GeoDataExtractor.service.CronService;
import eu.urbanage.GeoDataExtractor.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@CrossOrigin(origins = { "https://geodata-extractor-ui.dev.ecosystem-urbanage.eu",
        "https://geodata-extractor-ui.ecosystem-urbanage.eu", "https://gisviewer.santander.dev.ecosystem-urbanage.eu",
        "https://gisviewer.santander.ecosystem-urbanage.eu", "http://localhost:4200", "https://geocacher-dev.urbreath.tech" })
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

    @Value("${keycloak.url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @PostMapping("/save/")
    public String postDocument(@RequestBody Document docJson) {

        LOGGER.info("Received document store: " + docJson.getName());

        JsonNode userInfo = getUserInfoFromKeycloak(getAuthToken());

        docJson.setUserID(String.valueOf(userInfo.get("sub")));

        docJson.setUserEmail(String.valueOf(userInfo.get("email")));

        docJson.setOnIDRA(false);

        docJson.setDateCreation(new Date());

        ds.addDocument(docJson);

        return docJson.getId();

    }

    @PostMapping("/update/")
    public ResponseEntity<Document> updateDocument(@RequestBody Document docJson) {

        LOGGER.info("Received document update: " + docJson.getName());

        JsonNode userInfo = getUserInfoFromKeycloak(getAuthToken());

        docJson.setUserID(String.valueOf(userInfo.get("sub")));

        docJson.setUserEmail(String.valueOf(userInfo.get("email")));

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

            JsonNode userInfo = getUserInfoFromKeycloak(getAuthToken());

            return ds.findAllDocumentOfUser(String.valueOf(userInfo.get("sub")));

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

    private JsonNode getUserInfoFromKeycloak(String token) {
        RestTemplate restTemplate = new RestTemplate();
        String url = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Accept", "application/json");

        if (token != null && !token.startsWith("Bearer ")) {
            headers.set("Authorization", "Bearer " + token);
        } else {
            headers.set("Authorization", token);
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        return response.getBody();
    }

}
