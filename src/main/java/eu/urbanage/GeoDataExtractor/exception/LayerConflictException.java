package eu.urbanage.GeoDataExtractor.exception;

public class LayerConflictException extends RuntimeException{

    public LayerConflictException(String layer) {
        super("Layer conflict: '" + layer + "' already exists or is duplicated.");
    }
}
