package eu.urbanage.GeoDataExtractor.exception;

public class FeatureUpdateException extends RuntimeException{
    public FeatureUpdateException(String reason) {
        super("Feature update failed: " + reason);
    }
}
