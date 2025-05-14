package eu.urbanage.GeoDataExtractor.exception;

import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(FeatureNotFoundException.class)
    public ResponseEntity<?> handleFeatureNotFound(FeatureNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error",ex.getMessage()));
    }

    @ExceptionHandler(InvalidLayerNameException.class)
    public ResponseEntity<?> handleInvalidLayer(InvalidLayerNameException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({
        InvalidWorkspaceException.class,
        GeometryParsingException.class,
        GeoJsonFormatException.class,
        UnsupportedGeometryTypeException.class,
        LayerConflictException.class
    })
    public ResponseEntity<?> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({
        FeatureUpdateException.class,
        WorkspaceOperationException.class,
        DatastoreOperationException.class,
        GeoServerRequestException.class
    })
    public ResponseEntity<?> handleInternalError(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({
        LayerNotPublishedException.class,
        TableNotFoundException.class
    })
    public ResponseEntity<?> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedGeoServerAccessException.class)
    public ResponseEntity<?> handleUnauthorized(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
    }
}
