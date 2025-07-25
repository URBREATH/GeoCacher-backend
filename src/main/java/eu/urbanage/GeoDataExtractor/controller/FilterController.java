package eu.urbanage.GeoDataExtractor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import eu.urbanage.GeoDataExtractor.model.Filter;
import eu.urbanage.GeoDataExtractor.model.FilterDetail;
import eu.urbanage.GeoDataExtractor.service.FilterDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = { "https://geodata-extractor-ui.dev.ecosystem-urbanage.eu",
        "https://geodata-extractor-ui.ecosystem-urbanage.eu", "https://gisviewer.santander.dev.ecosystem-urbanage.eu",
        "https://gisviewer.santander.ecosystem-urbanage.eu", "http://localhost:4200" })
@RestController
@RequestMapping("/api/filter")
public class FilterController {

    @Autowired
    protected FilterDocumentService fsd;
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterController.class);

    @PostMapping("/")
    public ResponseEntity<FilterDetail> postFilter(@RequestBody String filterJson) {

        //LOGGER.info("Received filter request: " + filterJson);

        try {
            // Converting the JSON string back to a Filter object
            ObjectMapper objectMapper = new ObjectMapper();
            Filter filter = objectMapper.readValue(filterJson, Filter.class);
            
            return fsd.retriveFilterList(filter.getCityName());

        } catch (JsonProcessingException e) {
            LOGGER.error("Error processing filter JSON", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
