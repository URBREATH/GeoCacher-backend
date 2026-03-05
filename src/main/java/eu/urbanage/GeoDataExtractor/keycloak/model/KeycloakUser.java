package eu.urbanage.GeoDataExtractor.keycloak.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

public class KeycloakUser {

    private String sub;

    @JsonProperty("email_verified")
    private boolean emailVerified;

    private Set<String> roles;

    @JsonProperty("realm_access")
    private RealmAccess realmAccess;

    private String name;

    @JsonProperty("preferred_username")
    private String preferredUsername;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    private String email;

    public KeycloakUser() {
    }

    public KeycloakUser(String sub, boolean emailVerified, Set<String> roles, String name,
                        String preferredUsername, String givenName, String familyName, String email) {
        this.sub = sub;
        this.emailVerified = emailVerified;
        this.roles = roles;
        this.name = name;
        this.preferredUsername = preferredUsername;
        this.givenName = givenName;
        this.familyName = familyName;
        this.email = email;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public void setPreferredUsername(String preferredUsername) {
        this.preferredUsername = preferredUsername;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RealmAccess getRealmAccess() {
        return realmAccess;
    }

    public void setRealmAccess(RealmAccess realmAccess) {
        this.realmAccess = realmAccess;
    }

    @Override
    public String toString() {
        return "KeycloakUser [sub=" + sub + ", " + "email_verified=" + emailVerified + ", roles="
                + roles + ", name=" + name + ", preferred_username=" + preferredUsername + ", "
                + "given_name=" + givenName + ", family_name=" + familyName + ", email=" + email + "]";
    }

    public static class RealmAccess {
        private Set<String> roles;

        public Set<String> getRoles() {
            return roles;
        }

        public void setRoles(Set<String> roles) {
            this.roles = roles;
        }
    }
}
