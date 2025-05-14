package eu.urbanage.GeoDataExtractor.exception;

public class UnsupportedGeometryTypeException extends RuntimeException{

    public UnsupportedGeometryTypeException(String type) {
        super("Unsupported geometry type: " + type);
    }
}
