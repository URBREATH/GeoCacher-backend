package eu.urbanage.GeoDataExtractor.controller;

import eu.urbanage.GeoDataExtractor.model.Cron;
import eu.urbanage.GeoDataExtractor.model.Document;
import eu.urbanage.GeoDataExtractor.service.CronService;
import eu.urbanage.GeoDataExtractor.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/cron/")
public class CronController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private CronService cs;

    @Autowired
    protected DocumentService ds;

    @PostMapping("/set/")
    public ResponseEntity<String> postCron(@RequestBody Cron cronJson) {

        String document_id = cronJson.getDocument_id();

        Document ref_document = ds.findDocument(document_id).getBody();

        if (ref_document == null) {
            LOGGER.error("Document {} not found when creating cron", document_id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found: " + document_id);
        }

        cronJson.setCity(ref_document.getCityName());

        cronJson.setFilter(ref_document.getFilter());

        cronJson.setData_created(new Date());

        cronJson.setData_last_execution(new Date());

        Cron savedCron = cs.addCron(cronJson);

        if (savedCron == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save cron");
        }

        String cron_id = savedCron.getId();

        ref_document.setCron_id(cron_id);

        ds.updateDocumentFromCron(ref_document);

        return ResponseEntity.ok(cron_id);

    }

    @PostMapping("/update/")
    public ResponseEntity<Cron> updateCron(@RequestBody Cron cronJson) {

        String document_id = cronJson.getDocument_id();

        Document ref_document = ds.findDocument(document_id).getBody();

        if (ref_document == null) {
            LOGGER.error("Document {} not found when updating cron", document_id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        cronJson.setCity(ref_document.getCityName());

        cronJson.setFilter(ref_document.getFilter());

        cronJson.setData_created(new Date());

        cronJson.setData_last_execution(new Date());

        return cs.updateCron(cronJson);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Cron> getCron(@PathVariable() String id) {

        try {

            return cs.findCron(id);

        } catch (Exception e) {
            LOGGER.error(id, e);
            return ResponseEntity.internalServerError().body(null);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCron(@PathVariable() String id) {

        try {

            Document reletedDocument = ds.findDocumentObj(id);

            if (reletedDocument == null) {
                LOGGER.warn("Document {} not found when deleting cron", id);
                return ResponseEntity.notFound().build();
            }

            String cronID = reletedDocument.getCron_id();

            reletedDocument.setCron_id(null);

            ds.updateDocumentFromCron(reletedDocument);

            return cs.deleteCron(cronID);

        } catch (Exception e) {
            LOGGER.error("Error deleting cron for document {}", id, e);
            return ResponseEntity.internalServerError().body(null);
        }

    }

}
