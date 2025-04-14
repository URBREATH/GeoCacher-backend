package eu.urbanage.GeoDataExtractor.controller;


import eu.urbanage.GeoDataExtractor.service.GeoServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = { "https://geodata-extractor-ui.dev.ecosystem-urbanage.eu",
        "https://geodata-extractor-ui.ecosystem-urbanage.eu", "https://gisviewer.santander.dev.ecosystem-urbanage.eu",
        "https://gisviewer.santander.ecosystem-urbanage.eu", "http://localhost:4200" })
@RestController
@RequestMapping("/api/geoserver")
public class GeoServerController {

    @Autowired
    private GeoServerService geoServerService;

    @GetMapping("/getLayer")
    public ResponseEntity<String> getLayer(@RequestParam String layerName)    {
        String geojson = geoServerService.getLayerGeoJSON(layerName);
        return ResponseEntity.ok(geojson);
    }


    @PostMapping("/save")
    public ResponseEntity<?> saveGeoJsonToPostGIS(@RequestBody String geoJson) {
        try {
            geoServerService.saveGeoJson(geoJson);
            return ResponseEntity.ok("GeoJSON salvato nel database PostGIS.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    @PostMapping("/save2")
    public ResponseEntity<?> saveGeoJsonToPostGIS(@RequestParam String tableName,
                                                @RequestBody String geoJson) {
        try {
            geoServerService.saveGeoJson2(geoJson, tableName);
            return ResponseEntity.ok("GeoJSON saved to PostGIS in table: " + tableName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error while saving: " + e.getMessage());
        }
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publishExistingLayerToGeoServer(
            @RequestParam String workspace,
            @RequestParam String datastore,
            @RequestParam String layerName) {
        try {
            geoServerService.publishToGeoServer(workspace, datastore, layerName);
            return ResponseEntity.ok("Layer pubblicato su GeoServer.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nella pubblicazione su GeoServer: " + e.getMessage());
        }
    }


    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndPublish(@RequestParam String workspace,
                                                   @RequestParam String datastore,
                                                   @RequestParam String layer,
                                                   @RequestBody String geoJson) {
        try {
            geoServerService.saveGeoJson(geoJson);
            geoServerService.publishToGeoServer(workspace,datastore,layer);
            return ResponseEntity.ok("Salvataggio su PostGIS e pubblicazione su GeoServer completati.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore: " + e.getMessage());
        }
    }

    @GetMapping("/workspaces")
    public ResponseEntity<String> getGeoServerWorkspaces() {
        String workspaces = geoServerService.getWorkspaces();
        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/datastores")
    public ResponseEntity<String> getGeoServerDatastores(@RequestParam String workspace) {
        String datastores = geoServerService.getDatastores(workspace);
        return ResponseEntity.ok(datastores);
    }

    @PostMapping("/datastore")
    public ResponseEntity<?> createDatastore(
            @RequestParam String workspace,
            @RequestParam String datastore) {
        try {
            geoServerService.createPostGISDatastore(workspace, datastore);
            return ResponseEntity.ok("Datastore creato correttamente su GeoServer.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore creazione datastore: " + e.getMessage());
        }
    }

    @PostMapping("/workspace")
    public ResponseEntity<String> createWorkspace(@RequestParam String workspace) {
        try {
            geoServerService.createWorkspace(workspace);
            return ResponseEntity.ok("Workspace creato con successo: " + workspace);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nella creazione del workspace: " + e.getMessage());
        }
    }

    
}

