package eu.urbanage.GeoDataExtractor.exception;

public class GeoServerRequestException extends RuntimeException{

    public GeoServerRequestException(String message, Throwable cause) {
        super("GeoServer error: " + message, cause);
    }
}
