package eu.urbanage.GeoDataExtractor.exception;

public class GeoJsonFormatException extends RuntimeException{

    public GeoJsonFormatException(String reason) {
        super("Invalid GeoJSON format: " + reason);
    }
}
