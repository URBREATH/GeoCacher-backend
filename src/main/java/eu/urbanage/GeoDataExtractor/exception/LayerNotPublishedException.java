package eu.urbanage.GeoDataExtractor.exception;

public class LayerNotPublishedException extends RuntimeException{

    public LayerNotPublishedException(String layer) {
        super("Layer '" + layer + "' is not published or accessible in GeoServer.");
    }
}
