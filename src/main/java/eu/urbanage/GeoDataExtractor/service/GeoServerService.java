package eu.urbanage.GeoDataExtractor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.urbanage.GeoDataExtractor.utils.GeoServerQueryBuilder;
import org.locationtech.jts.geom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.jdbc.core.JdbcTemplate;



@Service
public class GeoServerService {

    @Value("${geoserver.url}")
    private String geoServerUrl;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    private JdbcTemplate jdbcTemplate;



    RestTemplate restTemplate=new RestTemplate();

    public String getWorkspaces() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "geoserver");        
        
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            geoServerUrl+"/rest/workspaces",
            HttpMethod.GET,
            entity,
            String.class
        );

        return response.getBody();
    }

    public String getDatastores(String workspace) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "geoserver");        
        
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            geoServerUrl+"/rest/workspaces/"+workspace+"/datastores",
            HttpMethod.GET,
            entity,
            String.class
        );

        return response.getBody();
    }

    public void createWorkspace(String workspaceName) {
        String url = geoServerUrl + "/rest/workspaces";
    
        String xmlPayload = """
            <workspace>
                <name>%s</name>
            </workspace>
            """.formatted(workspaceName);
    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setBasicAuth("admin", "geoserver"); // o usa configurazione
    
        HttpEntity<String> request = new HttpEntity<>(xmlPayload, headers);
    
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Errore creazione workspace: " + response.getBody());
        }
    }    

    public void createPostGISDatastore(String workspace, String datastoreName) {
        String url = geoServerUrl + "/rest/workspaces/" + workspace + "/datastores";
    
        String payload = """
            {
              "dataStore": {
                "name": "%s",
                "description": "Datastore PostGIS creato via API",
                "type": "PostGIS",
                "enabled": true,
                "connectionParameters": {
                  "host": "postgis",
                  "port": "5432",
                  "database": "ProvaPostGIS",
                  "user": "postgres",
                  "passwd": "postgres",
                  "dbtype": "postgis",
                  "schema": "public",
                  "Expose primary keys": "true",
                  "Loose bbox": "true",
                  "preparedStatements": "false",
                  "validate connections": "true",
                  "Max connections": "20",
                  "fetch size": "1000"
                }
              }
            }
            """.formatted(datastoreName);
    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("admin", "geoserver");
    
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
    
        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            String.class
        );
    
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Errore creazione datastore: " + response.getStatusCode() + " - " + response.getBody());
        }
    }
    

    public String getLayerGeoJSON(String layerName) {
        String url = new GeoServerQueryBuilder(geoServerUrl)
                .setGetFeatureRequest()
                .setTypeName(layerName)
                .setOutputFormat("application/json")
                .build();

        try {
            System.out.println("Chiamata a GeoServer: " + url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Errore nella richiesta a GeoServer:");
            e.printStackTrace(); // oppure usa un logger
            return "{ \"error\": \"" + e.getMessage() + "\" }";
        }
    }

    private Geometry parseGeometry(String type, JsonNode coords) {
        switch (type) {
            case "Point":
                return geometryFactory.createPoint(parseCoordinate(coords));
            case "Polygon":
                return geometryFactory.createPolygon(parseCoordinates(coords.get(0)));
            case "MultiPolygon":
                Polygon[] polygons = new Polygon[coords.size()];
                for (int i = 0; i < coords.size(); i++) {
                    polygons[i] = geometryFactory.createPolygon(parseCoordinates(coords.get(i).get(0)));
                }
                return geometryFactory.createMultiPolygon(polygons);
            default:
                throw new UnsupportedOperationException("Tipo non supportato: " + type);
        }
    }

    private Coordinate parseCoordinate(JsonNode coord) {
        // Es: [12.4924, 41.8902] => Coordinate(x=12.4924, y=41.8902)
        return new Coordinate(coord.get(0).asDouble(), coord.get(1).asDouble());
    }

    private Coordinate[] parseCoordinates(JsonNode array) {
        Coordinate[] coords = new Coordinate[array.size()];
        for (int i = 0; i < array.size(); i++) {
            coords[i] = parseCoordinate(array.get(i));
        }
        return coords;
    }

    public void publishToGeoServer(String workspace, String datastore, String layerName) {
        String url = geoServerUrl + "/rest/workspaces/" + workspace +
                "/datastores/" + datastore + "/featuretypes";

        String payload = """
            <featureType>
              <name>%s</name>
              <nativeName>%s</nativeName>
              <title>%s</title>
            <srs>EPSG:4326</srs>
            <enabled>true</enabled>
            <metadata>
              <entry key="recalculate">true</entry>
            </metadata>
            </featureType>
            """.formatted(layerName, layerName, layerName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setBasicAuth("admin", "geoserver");

        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<String> response = new RestTemplate().postForEntity(url, request, String.class);
    
            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println(">>> GEO ERROR: " + response.getStatusCode());
                System.err.println(">>> GEO BODY: " + response.getBody());
                throw new RuntimeException("Errore nella pubblicazione su GeoServer: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println(">>> EXCEPTION GEO: " + e.getMessage());
            throw new RuntimeException("Errore nella pubblicazione su GeoServer (eccezione): " + e.getMessage(), e);
        }
    }

    public void saveGeoJson(String geoJson, String tableName) {
        try {
            // Sanitize table name (basic)
            if (!tableName.matches("[a-zA-Z0-9_]+")) {
                throw new IllegalArgumentException("Invalid table name: " + tableName);
            }
    
            // Step 1 – Create table if not exists
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    id SERIAL PRIMARY KEY,
                    geometry geometry(Geometry,4326),
                    properties TEXT
                );
                """.formatted(tableName);
            jdbcTemplate.execute(createTableSQL);
    
            // Step 2 – Parse GeoJSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(geoJson);
            JsonNode features = root.get("features");
    
            for (JsonNode feature : features) {
                JsonNode geometryNode = feature.get("geometry");
                JsonNode coords = geometryNode.get("coordinates");
                String type = geometryNode.get("type").asText();
    
                if (coords == null || coords.isNull()) {
                    throw new IllegalArgumentException("Missing coordinates in GeoJSON");
                }
    
                Geometry geom = parseGeometry(type, coords);
                geom.setSRID(4326);
                String properties = mapper.writeValueAsString(feature.get("properties"));
    
                // Step 3 – Insert data
                String insertSQL = "INSERT INTO " + tableName + " (geometry, properties) VALUES (ST_GeomFromText(?, 4326), ?)";
                jdbcTemplate.update(insertSQL, geom.toText(), properties);


            }
    
        } catch (Exception e) {
            throw new RuntimeException("Failed to process GeoJSON: " + e.getMessage(), e);
        }
    }
    

}