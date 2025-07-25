package eu.urbanage.GeoDataExtractor.exception;

public class TableNotFoundException extends RuntimeException{

    public TableNotFoundException(String table) {
        super("Table '" + table + "' not found in PostGIS.");
    }
}
