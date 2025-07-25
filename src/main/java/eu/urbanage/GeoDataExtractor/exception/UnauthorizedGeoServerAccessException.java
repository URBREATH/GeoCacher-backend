package eu.urbanage.GeoDataExtractor.exception;

public class UnauthorizedGeoServerAccessException extends RuntimeException{

    public UnauthorizedGeoServerAccessException() {
        super("Unauthorized access to GeoServer. Check credentials.");
    }
}
