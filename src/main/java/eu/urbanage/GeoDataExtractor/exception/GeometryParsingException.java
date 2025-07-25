package eu.urbanage.GeoDataExtractor.exception;

public class GeometryParsingException extends RuntimeException{

    public GeometryParsingException(String type) {
        super("Unsupported or malformed geometry type: " + type);
    }
}
