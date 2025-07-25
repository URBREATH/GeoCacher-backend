package eu.urbanage.GeoDataExtractor.exception;

public class FeatureNotFoundException extends RuntimeException {
    public FeatureNotFoundException(int id, String layerName) {
        super("Feature with id " + id + " not found in layer: " + layerName);
    }
}
