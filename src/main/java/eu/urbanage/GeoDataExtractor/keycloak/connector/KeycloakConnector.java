package eu.urbanage.GeoDataExtractor.keycloak.connector;

import eu.urbanage.GeoDataExtractor.keycloak.model.KeycloakUser;
import eu.urbanage.GeoDataExtractor.keycloak.model.Token;

public interface KeycloakConnector {
    KeycloakUser getUserFromToken(String token);
    String getLoginUrl();
    Token getToken(String code);
}
