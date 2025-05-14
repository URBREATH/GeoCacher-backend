package eu.urbanage.GeoDataExtractor.exception;

public class InvalidLayerNameException extends RuntimeException{
    
    public InvalidLayerNameException(String LayerName) {
        super ("Invalid layer name: " + LayerName);
    }
}
