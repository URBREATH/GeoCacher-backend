package eu.urbanage.GeoDataExtractor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.urbanage.GeoDataExtractor.utils.OrionQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FilterService implements FilterClient{

    private static final Logger log = LoggerFactory.getLogger(FilterService.class);

    RestTemplate restTemplate = new RestTemplate();

    @Value("${HOST_ORION}")
    private String hostContextBroker;


    public FilterService(@Value("${HOST_ORION}") String hostContextBroker) {
        this.hostContextBroker = hostContextBroker;
    }

    public List<String> getAllCityFilter(String city) throws JsonProcessingException {

        log.info("Starting filter discovery for city: {}", city);
        OrionQueryBuilder oqb = new OrionQueryBuilder(hostContextBroker + "/ngsi-ld/v1/types");
        String contexBrokerEndpoint = oqb.get();
        log.debug("Requesting all entity types from: {}", contexBrokerEndpoint);
        
        ResponseEntity<String> response = restTemplate.getForEntity(contexBrokerEndpoint, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode nameNode = mapper.readTree(response.getBody());

        List<String> filterList = new ArrayList<String>();

        for(JsonNode filter_node : nameNode.get("typeList")){

            String filter_ = filter_node.asText();

            OrionQueryBuilder oqbCheck = new OrionQueryBuilder(hostContextBroker, 1);
            String contexBrokerQuery = oqbCheck.addIdPattern(filter_, city).addLocationQuery().get();
            log.debug("Checking for entities of type '{}' with query: {}", filter_, contexBrokerQuery);

            try {
                ResponseEntity<String> checkResponse = restTemplate.getForEntity(contexBrokerQuery, String.class);
                JsonNode responseBody = mapper.readTree(checkResponse.getBody());

                // The NGSI-LD query returns an array. If it's not empty, we've found at least one entity.
                if (responseBody.isArray() && !responseBody.isEmpty()){
                    filterList.add(filter_);
                    log.debug("Type '{}' is available for city '{}'. Added to list.", filter_, city);
                }

            } catch (HttpClientErrorException.NotFound ex) {
                // This is a normal case: no entity of this type found for the city.
                log.trace("No entities of type '{}' found for city '{}'. Skipping.", filter_, city);
            } catch (RestClientException | JsonProcessingException e) {
                // Log other errors (e.g., network issues, malformed JSON) but continue the loop.
                log.error("Error while checking type '{}' for city '{}': {}", filter_, city, e.getMessage());
            }
        }

        log.info("Found {} available filters for city {}: {}", filterList.size(), city, filterList);
        return filterList;
    }

    public List<String> getEntityAttributes(String entityType, String city) {
        log.info("Discovering ID subtypes for entity type '{}' in city '{}'", entityType, city);

        // 1. Build a query to get ALL entities of a certain type, asking only for their 'id'.
        // We use a high limit to make sure we get them all.
        OrionQueryBuilder oqb = new OrionQueryBuilder(hostContextBroker, 1000);
        String queryUrl = oqb.addIdPattern(entityType, city)
                             .get();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(queryUrl, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseBody = mapper.readTree(response.getBody());

            if (responseBody.isArray() && !responseBody.isEmpty()) {
                // 2. Iterate over each entity, extract the ID, and parse it.
                return StreamSupport.stream(responseBody.spliterator(), false)
                        .map(entity -> entity.get("id").asText())
                        .map(id -> {
                            // The ID is in the format "urn:ngsi-ld:<type>:<city>:<subtype>:<specific_id>"
                            // Example: "urn:ngsi-ld:PointOfInterest:Parma:platform:2360885904"
                            String[] parts = id.split(":");
                            if (parts.length > 4) {
                                // We extract the part that interests us, which is the fourth segment (index 4)
                                return parts[4];
                            }
                            return null; // Ignore non-compliant IDs
                        })
                        .filter(subtype -> subtype != null && !subtype.isEmpty()) // Remove nulls and empty strings
                        .distinct() // Take only unique values
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Could not discover ID subtypes for type '{}': {}", entityType, e.getMessage());
        }
        return List.of();
    }

}
