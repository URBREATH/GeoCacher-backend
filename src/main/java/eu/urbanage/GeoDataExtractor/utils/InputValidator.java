package eu.urbanage.GeoDataExtractor.utils;

import eu.urbanage.GeoDataExtractor.exception.DatastoreOperationException;
import eu.urbanage.GeoDataExtractor.exception.InvalidLayerNameException;
import eu.urbanage.GeoDataExtractor.exception.InvalidWorkspaceException;

public class InputValidator {
    private static final String NAME_PATTERN = "[a-zA-Z0-9_]+";

    public static void validateWorkspace(String workspace) {
        if (!workspace.matches(NAME_PATTERN)) {
            throw new InvalidWorkspaceException(workspace);
        }
    }

    public static void validateDatastore(String datastore) {
        if (!datastore.matches(NAME_PATTERN)) {
            throw new DatastoreOperationException(datastore, "Invalid datastore name.");
        }
    }

    public static void validateLayerName(String layerName) {
        if (!layerName.matches(NAME_PATTERN)) {
            throw new InvalidLayerNameException(layerName);
        }
    }

    
}

