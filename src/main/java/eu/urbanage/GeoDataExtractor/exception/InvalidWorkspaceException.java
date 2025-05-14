package eu.urbanage.GeoDataExtractor.exception;

public class InvalidWorkspaceException extends RuntimeException {

    public InvalidWorkspaceException(String workspace) {
        super("Invalid or missing workspace: " + workspace);
    }
}
