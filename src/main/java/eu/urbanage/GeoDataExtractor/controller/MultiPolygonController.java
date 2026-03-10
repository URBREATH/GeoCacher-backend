package eu.urbanage.GeoDataExtractor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.urbanage.GeoDataExtractor.model.MultiPolygon;
import eu.urbanage.GeoDataExtractor.service.GeojsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/multipolygondata")
public class MultiPolygonController {

    @Autowired
    private GeojsonService gs;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiPolygonController.class);

    @PostMapping("/")
    public ResponseEntity<String> postMultiPolygon(@RequestBody String polygonJson) {

        try {
            // Converting the JSON string back to a Polygon object
            ObjectMapper objectMapper = new ObjectMapper();
            MultiPolygon multiPolygon = objectMapper.readValue(polygonJson, MultiPolygon.class);

            List<String> test = gs.getFromMultiPolygon(multiPolygon);

            return ResponseEntity.ok().body(test.toString());

        } catch (JsonProcessingException e) {
            LOGGER.error("Error processing polygon JSON", e);
            return ResponseEntity.badRequest().body("Error processing polygon JSON");
        }
    }

}
