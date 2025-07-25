package eu.urbanage.GeoDataExtractor.exception;

public class WorkspaceOperationException extends RuntimeException{

    public WorkspaceOperationException(String name, String reason) {
        super("Workspace '" + name + "' operation failed: " + reason);
    }
}
