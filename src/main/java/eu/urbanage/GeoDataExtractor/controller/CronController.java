package eu.urbanage.GeoDataExtractor.controller;

import eu.urbanage.GeoDataExtractor.model.Cron;
import eu.urbanage.GeoDataExtractor.model.Document;
import eu.urbanage.GeoDataExtractor.service.CronService;
import eu.urbanage.GeoDataExtractor.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin(origins = { "https://geodata-extractor-ui.dev.ecosystem-urbanage.eu",
        "https://geodata-extractor-ui.ecosystem-urbanage.eu", "https://gisviewer.santander.dev.ecosystem-urbanage.eu",
        "https://gisviewer.santander.ecosystem-urbanage.eu", "http://localhost:4200", "https://geocacher-dev.urbreath.tech" })
@RestController
@RequestMapping("/api/cron/")
public class CronController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private CronService cs;

    @Autowired
    protected DocumentService ds;

    @PostMapping("/set/")
    public String postCron(@RequestBody Cron cronJson) {

        String document_id = cronJson.getDocument_id();

        Document ref_document = ds.findDocument(document_id).getBody();

        cronJson.setCity(ref_document.getCityName());

        cronJson.setFilter(ref_document.getFilter());

        cronJson.setData_created(new Date());

        cronJson.setData_last_execution(new Date());

        String cron_id = cs.addCron(cronJson).getId();

        ref_document.setCron_id(cron_id);

        ds.updateDocumentFromCron(ref_document);

        return cron_id;

    }

    @PostMapping("/update/")
    public ResponseEntity<Cron> updateCron(@RequestBody Cron cronJson) {

        String document_id = cronJson.getDocument_id();

        Document ref_document = ds.findDocument(document_id).getBody();

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

            String cronID = reletedDocument.getCron_id();

            reletedDocument.setCron_id(null);

            ds.updateDocumentFromCron(reletedDocument);

            return cs.deleteCron(cronID);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }

    }

}
