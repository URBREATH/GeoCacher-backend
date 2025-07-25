package eu.urbanage.GeoDataExtractor.exception;

public class DatastoreOperationException extends RuntimeException{

    public DatastoreOperationException(String name, String reason) {
        super("Datastore '" + name + "' operation failed: " + reason);
    }
}
