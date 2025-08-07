package eu.urbanage.GeoDataExtractor.controller;

import eu.urbanage.GeoDataExtractor.model.Document;
import eu.urbanage.GeoDataExtractor.service.DocumentService;
import eu.urbanage.GeoDataExtractor.service.IDRAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = { "https://geodata-extractor-ui.dev.ecosystem-urbanage.eu",
        "https://geodata-extractor-ui.ecosystem-urbanage.eu", "https://gisviewer.santander.dev.ecosystem-urbanage.eu",
        "https://gisviewer.santander.ecosystem-urbanage.eu", "http://localhost:4200",  "https://geocacher-dev.urbreath.tech" })
@RestController
@RequestMapping("/api/idra/")
public class IDRAController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    protected DocumentService ds;

    @Autowired
    protected IDRAService is;

    @GetMapping("/{id}")
    public ResponseEntity<String> getCron(@PathVariable() String id) {

        try {

            Document selDoc = ds.findDocument(id).getBody();

            int idraRespone = is.postOnIDRA(selDoc);

            if (idraRespone == 201) {
                selDoc.setOnIDRA(true);
                ds.updateDocument(selDoc);
            }

            return ResponseEntity.status(idraRespone).body(id);

        } catch (Exception e) {
            LOGGER.error(id, e);
            return ResponseEntity.internalServerError().body(null);
        }

    }

}
