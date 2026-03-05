package eu.urbanage.GeoDataExtractor.controller;

import eu.urbanage.GeoDataExtractor.keycloak.connector.KeycloakConnector;
import eu.urbanage.GeoDataExtractor.keycloak.model.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = { "https://geodata-extractor-ui.dev.ecosystem-urbanage.eu",
        "https://geodata-extractor-ui.ecosystem-urbanage.eu", "https://gisviewer.santander.dev.ecosystem-urbanage.eu",
        "https://gisviewer.santander.ecosystem-urbanage.eu", "http://localhost:4200", "https://geocacher-dev.urbreath.tech" })
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final KeycloakConnector keycloakConnector;

    public AuthController(KeycloakConnector keycloakConnector) {
        this.keycloakConnector = keycloakConnector;
    }

    @GetMapping("/login-url")
    public ResponseEntity<Map<String, String>> getLoginUrl() {
        Map<String, String> response = new HashMap<>();
        response.put("loginUrl", keycloakConnector.getLoginUrl());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/token")
    public ResponseEntity<Token> getToken(@RequestParam("code") String code) {
        try {
            Token token = keycloakConnector.getToken(code);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}
