package eu.urbanage.GeoDataExtractor.controller;


import eu.urbanage.GeoDataExtractor.service.GeoServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;





@RestController
@RequestMapping("/api/geoserver")
public class GeoServerController {

    @Autowired
    private GeoServerService geoServerService;

    @GetMapping("/layers/{workspace}/{layerName}")
    public ResponseEntity<String> getLayer(@PathVariable String workspace, @PathVariable String layerName)    {
        String geojson = geoServerService.getLayerGeoJSON(workspace,layerName);
        return ResponseEntity.ok(geojson);
    }

    @GetMapping("/layers")
    public ResponseEntity<String> getAllLayers(@RequestParam String workspace,
                                @RequestParam String datastore) {

        String layersJson = geoServerService.getLayers(workspace, datastore);
        return ResponseEntity.ok(layersJson);
    }
    

    @PostMapping("/layers")
    public ResponseEntity<?> uploadAndPublish(@RequestParam String workspace,
                                                   @RequestParam String datastore,
                                                   @RequestParam String layer) {
                                                    
        geoServerService.createTable(layer);
        geoServerService.publishToGeoServer(workspace,datastore,layer);
        return ResponseEntity.ok("Layer saved on PostGIS and published on Geoserver.");
    }

    @DeleteMapping("/layers/{layerName}") 
    public ResponseEntity<?> deleteLayer(@PathVariable String layerName, 
                                        @RequestParam String workspace, 
                                        @RequestParam String datastore){
            
        geoServerService.deleteLayer(workspace, datastore, layerName);
        return ResponseEntity.ok("Layer '" + layerName + "' deleted successfully.");
    }

    @PostMapping("/features")
    public ResponseEntity<?> saveGeoJsonToPostGIS(@RequestParam String layer,
                                                @RequestBody String geoJson) {
        geoServerService.saveGeoJson(geoJson, layer);
        return ResponseEntity.ok("GeoJSON saved to PostGIS in table: " + layer);
    }

    @GetMapping("/features/{id}")
    public ResponseEntity<String> getFeatureById(@PathVariable int id , @RequestParam String workspace, @RequestParam String layerName) {
        String feature = geoServerService.getFeatureById(id, workspace, layerName);
        return ResponseEntity.ok(feature);
    } 

    @PutMapping("/features/{id}")
    public ResponseEntity<String> updateFeature(
            @PathVariable int id,
            @RequestParam String layerName,
            @RequestBody JsonNode featureBody) {

        geoServerService.updateFeature(layerName, id, featureBody);
        return ResponseEntity.ok("Feature updated");
        
    }

    @DeleteMapping("/features/{id}")
    public ResponseEntity<String> deleteFeatureById(
            @PathVariable int id,
            @RequestParam String layerName) {
    
        geoServerService.deleteFeatureById(layerName, id);
        return ResponseEntity.ok("Feature with id " + id + " deleted from table " + layerName);
    }

    @DeleteMapping("/features")
    public ResponseEntity<String> deleteAllFeatures(@RequestParam String layerName) {

        geoServerService.deleteAllFeatures(layerName);
        return ResponseEntity.ok("All features deleted from table " + layerName);
    }

    @PostMapping("/workspaces")
    public ResponseEntity<String> createWorkspace(@RequestParam String workspace) {

        geoServerService.createWorkspace(workspace);
        return ResponseEntity.ok("Workspace created: " + workspace);
    }

    @GetMapping("/workspaces")
    public ResponseEntity<String> getGeoServerWorkspaces() {
        String workspaces = geoServerService.getWorkspaces();
        return ResponseEntity.ok(workspaces);
    }

    @DeleteMapping("/workspaces/{workspaceName}")
    public ResponseEntity<String> deleteWorkspace(@PathVariable String workspaceName) {

        geoServerService.deleteWorkspace(workspaceName);
        return ResponseEntity.ok("Workspace deleted: " + workspaceName);
    }

    @PostMapping("/datastores")
    public ResponseEntity<?> createDatastore(
            @RequestParam String workspace,
            @RequestParam String datastore) {

        geoServerService.createPostGISDatastore(workspace, datastore);
        return ResponseEntity.ok("Datastore created: " + datastore);
    }

    @GetMapping("/datastores")
    public ResponseEntity<String> getGeoServerDatastores(@RequestParam String workspace) {
        String datastores = geoServerService.getDatastores(workspace);
        return ResponseEntity.ok(datastores);
    }

    @DeleteMapping("/datastores/{datastoreName}")
    public ResponseEntity<String> deleteDatastore(
            @PathVariable String datastoreName,
            @RequestParam String workspace) {

        geoServerService.deleteDatastore(workspace, datastoreName);
        return ResponseEntity.ok("Datastore deleted: " + datastoreName);
    }

    
}
