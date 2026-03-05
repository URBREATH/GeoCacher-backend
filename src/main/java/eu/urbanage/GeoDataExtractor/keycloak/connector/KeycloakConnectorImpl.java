package eu.urbanage.GeoDataExtractor.keycloak.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.urbanage.GeoDataExtractor.keycloak.model.KeycloakUser;
import eu.urbanage.GeoDataExtractor.keycloak.model.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Service
public class KeycloakConnectorImpl implements KeycloakConnector {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${keycloak.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    public KeycloakConnectorImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getLoginUrl() {
        return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=openid";
    }

    @Override
    public Token getToken(String code) {
        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("code", code);
        map.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Token> response = restTemplate.postForEntity(tokenUrl, request, Token.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to retrieve token from Keycloak");
        }
    }

    @Override
    public KeycloakUser getUserFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token format");
        }

        String jwtToken = token.substring(7);
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] chunks = jwtToken.split("\\.");

        if (chunks.length < 2) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        String payload = new String(decoder.decode(chunks[1]));
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode userNode = mapper.readTree(payload);
            KeycloakUser user = new KeycloakUser();

            if (userNode.has("sub")) user.setSub(userNode.get("sub").asText());
            if (userNode.has("email_verified")) user.setEmailVerified(userNode.get("email_verified").asBoolean());
            if (userNode.has("name")) user.setName(userNode.get("name").asText());
            if (userNode.has("preferred_username")) user.setPreferredUsername(userNode.get("preferred_username").asText());
            if (userNode.has("given_name")) user.setGivenName(userNode.get("given_name").asText());
            if (userNode.has("family_name")) user.setFamilyName(userNode.get("family_name").asText());
            if (userNode.has("email")) user.setEmail(userNode.get("email").asText());

            if (userNode.has("realm_access") && userNode.get("realm_access").has("roles")) {
                Set<String> roles = new HashSet<>();
                userNode.get("realm_access").get("roles").forEach(role -> roles.add(role.asText()));
                user.setRoles(roles);
            }

            return user;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing token payload", e);
        }
    }
}
