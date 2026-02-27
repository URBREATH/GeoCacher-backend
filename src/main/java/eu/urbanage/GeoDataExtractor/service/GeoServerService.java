package eu.urbanage.GeoDataExtractor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.urbanage.GeoDataExtractor.exception.*;
import eu.urbanage.GeoDataExtractor.utils.GeoServerQueryBuilder;
import eu.urbanage.GeoDataExtractor.utils.InputValidator;
import eu.urbanage.GeoDataExtractor.utils.ParsingGeoJson;
import org.locationtech.jts.geom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.jdbc.core.JdbcTemplate;



@Service
public class GeoServerService {

    @Value("${geoserver.url}")
    private String geoServerUrl;
    @Autowired
    private JdbcTemplate jdbcTemplate;



    RestTemplate restTemplate=new RestTemplate();


    public void createWorkspace(String workspace) {
        InputValidator.validateWorkspace(workspace);
        String url = geoServerUrl + "/rest/workspaces";
    
        String xmlPayload = """
            <workspace>
                <name>%s</name>
            </workspace>
            """.formatted(workspace);
    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setBasicAuth("admin", "geoserver"); // or use configuration
    
        HttpEntity<String> request = new HttpEntity<>(xmlPayload, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new WorkspaceOperationException(workspace,response.getBody());
            }
        } catch (HttpClientErrorException.Conflict e) {
        throw new LayerConflictException("Workspace '" + workspace + "' already exists.");
        } catch (RestClientException e) {
            throw new GeoServerRequestException("Error creating workspace '" + workspace + "'", e);
        }
        
    }  

    public String getWorkspaces() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "geoserver");        
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
    
            ResponseEntity<String> response = restTemplate.exchange(
                geoServerUrl+"/rest/workspaces",
                HttpMethod.GET,
                entity,
                String.class);

            return response.getBody();
        } catch (RestClientException e) {
            throw new GeoServerRequestException("Failed to retrieve workspaces from Geoserver",e);
        }
    }
    
    public void deleteWorkspace(String workspace) {
        InputValidator.validateWorkspace(workspace);
        String url = geoServerUrl + "/rest/workspaces/" + workspace + "?recurse=true";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "geoserver");
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
        restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
        } catch (RestClientException e) {
            throw new WorkspaceOperationException(workspace, "Failed to delete workspace: " + e.getMessage());
        }
    }

    public void createPostGISDatastore(String workspace, String datastore) {
        InputValidator.validateWorkspace(workspace);
        InputValidator.validateDatastore(datastore);

        String url = geoServerUrl + "/rest/workspaces/" + workspace + "/datastores";
    
        String payload = """
            {
              "dataStore": {
                "name": "%s",
                "description": "PostGIS Datastore",
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
            """.formatted(datastore);
    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("admin", "geoserver");
    
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        try{
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new DatastoreOperationException(datastore, response.getBody());
            }
        } catch (RestClientException e) {
            throw new GeoServerRequestException("Failed to create datastore '" + datastore + "' in" + workspace + "'",e);
        }
    }

    public String getDatastores(String workspace) {
        InputValidator.validateWorkspace(workspace);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "geoserver");        
        
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
            geoServerUrl+"/rest/workspaces/"+workspace+"/datastores", HttpMethod.GET, entity, String.class);

            return response.getBody();
        } catch (RestClientException e) {
            throw new GeoServerRequestException("Failed to retrieve datastores for workspace '" + workspace + "'", e);
        }
        
    }
    
    public void deleteDatastore(String workspace, String datastore) {
        InputValidator.validateWorkspace(workspace);
        InputValidator.validateDatastore(datastore);
        String url = geoServerUrl + "/rest/workspaces/" + workspace + "/datastores/" + datastore + "?recurse=true";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "geoserver");
        try {
            HttpEntity<Void> request = new HttpEntity<>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
        } catch (RestClientException e){
            throw new DatastoreOperationException(datastore, "Failed to delete datastore from GeoServer: " + e.getMessage());
        }
        
    }
    

    public String getLayerGeoJSON(String workspace, String layerName) {
        InputValidator.validateWorkspace(workspace);
        InputValidator.validateLayerName(layerName);

        String url = new GeoServerQueryBuilder(geoServerUrl)
                .setGetFeatureRequest()
                .setTypeName(workspace + ":" + layerName)
                .setOutputFormat("application/json")
                .build();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new GeoServerRequestException("Error in WFS request for layer: " + layerName, e);
        }
    }

    public String getLayers(String workspace, String datastore) {
        InputValidator.validateWorkspace(workspace);
        InputValidator.validateDatastore(datastore);
        String url= geoServerUrl + "/rest/workspaces/" + workspace + 
                    "/datastores/" + datastore + "/featuretypes.json";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(MediaType.parseMediaTypes("application/json"));
        headers.setBasicAuth("admin", "geoserver");   // TODO: handle authorization

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            return response.getBody();
        } catch (RestClientException e) {
            throw new GeoServerRequestException("Unable to retrieve layers for workspace '" + workspace + "', datastore '" + datastore + "'", e);
        }
        

    }

    public void publishToGeoServer(String workspace, String datastore, String layerName) {
        InputValidator.validateWorkspace(workspace);
        InputValidator.validateDatastore(datastore);
        InputValidator.validateLayerName(layerName);

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
        headers.setBasicAuth("admin", "geoserver");   // TODO: handle authorization

        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<String> response = new RestTemplate().postForEntity(url, request, String.class);
        
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new LayerNotPublishedException(layerName + ": " + response.getBody());
            }
        } catch (RestClientException e) {
            throw new GeoServerRequestException("Failed to publish layer '" + layerName + "' to Geoserver", e);
        }
        
    }

    public void saveGeoJson(String geoJson, String tableName) {
        InputValidator.validateLayerName(tableName);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(geoJson);
        } catch (JsonProcessingException e) {
            throw new GeoJsonFormatException("Malformed GeoJSON: " + e.getMessage());
        }
        JsonNode features = root.get("features");
        if (features == null || !features.isArray()) {  // check if this condition is correct
            throw new GeoJsonFormatException("Missing or invalid 'features' array in GeoJSON");
        }
            
        ParsingGeoJson parser = new ParsingGeoJson();

        for (JsonNode feature : features) {
            try {
                JsonNode geometryNode = feature.get("geometry");
                if (geometryNode ==null)  {
                    throw new GeoJsonFormatException("Missing geometry in feature");
                }
                JsonNode coords = geometryNode.get("coordinates");
                String type = geometryNode.get("type").asText();
    
                
                Geometry geom = parser.parseGeometry(type, coords);
                geom.setSRID(4326);

                String properties = mapper.writeValueAsString(feature.get("properties"));
    
                String insertSQL = "INSERT INTO " + tableName + " (geometry, properties) VALUES (ST_GeomFromText(?, 4326), ?)";
                jdbcTemplate.update(insertSQL, geom.toText(), properties);
            } catch (UnsupportedGeometryTypeException e) {
            throw e; // already custom
            } catch (JsonProcessingException e) {
                throw new GeoJsonFormatException("Invalid properties format: " + e.getMessage());
            } catch (DataAccessException e) {
                throw new FeatureUpdateException("Failed DB insert into table " + tableName + ": " + e.getMessage());
            } catch (Exception e) {
                throw new FeatureUpdateException("Unexpected error while processing feature: " + e.getMessage());
            }
        }
    }

    public String getFeatureById(int id, String workspace, String layerName) {
        InputValidator.validateWorkspace(workspace);
        InputValidator.validateLayerName(layerName);
        String url= new GeoServerQueryBuilder(geoServerUrl)
                    .setGetFeatureRequest()
                    .setTypeName(workspace+":"+layerName)
                    .setOutputFormat("application/json")
                    .setCqlFilter("id="+id)
                    .setCount(1)
                    .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "geoserver");
        headers.setAccept(MediaType.parseMediaTypes("application/json"));

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            String body = response.getBody();

            if (body == null || body.contains("\"totalFeatures\" : 0") || body.contains("\"features\": []")) {
                throw new FeatureNotFoundException(id, layerName);
            }

            return body;
        } catch (RestClientException e) {
            throw new GeoServerRequestException("Error retrieving feature with id " + id + " from layer '" + layerName + "'", e);
        }
        
    }

    public void updateFeature(String layerName, int id, JsonNode featureJson) {
        InputValidator.validateLayerName(layerName);

        JsonNode geometryNode = featureJson.get("geometry");
        if (geometryNode == null) {
            throw new GeoJsonFormatException("Missing geometry field in features");
        }

        try {
            
            JsonNode propertiesNode = featureJson.get("properties");
            ParsingGeoJson parser= new ParsingGeoJson();
            Geometry geometry = parser.parseGeometry(
                    geometryNode.get("type").asText(),
                    geometryNode.get("coordinates")
            );
            geometry.setSRID(4326);

            String propertiesJson = new ObjectMapper().writeValueAsString(propertiesNode);

            String sql = "UPDATE " + layerName + " SET geometry = ST_GeomFromText(?, 4326), properties = ? WHERE id = ?";
            int updated = jdbcTemplate.update(sql, geometry.toText(), propertiesJson, id);

            if (updated == 0) {
                throw new FeatureNotFoundException(id, layerName);
            }

        } catch (UnsupportedGeometryTypeException e) {
            throw e;
        } catch (JsonProcessingException e) {
            throw new GeoJsonFormatException("Invalid propertie JSON: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new FeatureUpdateException("Database error while updating feature " + id + " in table " + layerName + ": " + e.getMessage());
        }
    }

    public void deleteFeatureById(String tableName, int id) {
        InputValidator.validateLayerName(tableName);
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try {
            int affected = jdbcTemplate.update(sql, id);
            if (affected == 0) {
                throw new FeatureNotFoundException(id, tableName);
            }
        } catch (DataAccessException e) {
           throw new FeatureUpdateException("Error deleting feature " + id + " from the layer " + tableName + ": " + e.getMessage());
        }
        
    }

    public void deleteAllFeatures(String tableName) {
        InputValidator.validateLayerName(tableName);
        try {
            String sql = "DELETE FROM " + tableName;
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            throw new FeatureUpdateException("Error deleting all features from table '" + tableName + "': " + e.getMessage());
        }
    }

    public void createTable(String tableName) {
        InputValidator.validateLayerName(tableName);

        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS %s (
                id SERIAL PRIMARY KEY,
                geometry geometry(Geometry,4326),
                properties TEXT
            );
            """.formatted(tableName);
        try {
            jdbcTemplate.execute(createTableSQL);
        } catch (DataAccessException e) {
            throw new FeatureUpdateException("Error creating table '" + tableName + "': " + e.getMessage());
        }

    }

    public void deleteLayer(String workspace, String datastore, String layerName) {
        InputValidator.validateWorkspace(workspace);
        InputValidator.validateDatastore(datastore);
        InputValidator.validateLayerName(layerName);

        String layerUrl = geoServerUrl + "/rest/layers/" + workspace + ":" + layerName;
        String featureTypeUrl = geoServerUrl + "/rest/workspaces/" + workspace + "/datastores/"+ datastore + "/featuretypes/"+ layerName;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin","geoserver");  // TODO: handle authorization
        HttpEntity<Void> request = new HttpEntity<>(headers);
        
        try {
            restTemplate.exchange(layerUrl, HttpMethod.DELETE, request, String.class);
            restTemplate.exchange(featureTypeUrl, HttpMethod.DELETE, request, String.class);
        } catch (RestClientException e) {
            throw new GeoServerRequestException("Error deleting layer or featureType from GeoServer: " + e.getMessage(), e);
        }
        String sql = "DROP TABLE IF EXISTS " + layerName + " CASCADE";

        try {
            jdbcTemplate.execute(sql);
        } catch (DataAccessException e) {
            throw new FeatureUpdateException("Error dropping table '" + layerName + "': " + e.getMessage());
        }
    }

}