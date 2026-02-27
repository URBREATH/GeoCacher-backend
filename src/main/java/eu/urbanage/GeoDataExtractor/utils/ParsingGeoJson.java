package eu.urbanage.GeoDataExtractor.utils;
import com.fasterxml.jackson.databind.JsonNode;
import org.locationtech.jts.geom.*;

public class ParsingGeoJson {

    private final GeometryFactory geometryFactory;

    public ParsingGeoJson() {
        this.geometryFactory = new GeometryFactory();
    }

    public Geometry parseGeometry(String type, JsonNode coords) {
        switch (type) {
            case "Point":
                return geometryFactory.createPoint(parseCoordinate(coords));
            case "Polygon":
                return geometryFactory.createPolygon(parseCoordinates(coords.get(0)));
            case "MultiPolygon":
                Polygon[] polygons = new Polygon[coords.size()];
                for (int i = 0; i < coords.size(); i++) {
                    polygons[i] = geometryFactory.createPolygon(parseCoordinates(coords.get(i).get(0)));
                }
                return geometryFactory.createMultiPolygon(polygons);
            default:
                throw new UnsupportedOperationException("Unsupported type: " + type);
        }
    }

    private Coordinate parseCoordinate(JsonNode coord) {
        return new Coordinate(coord.get(0).asDouble(), coord.get(1).asDouble());
    }

    private Coordinate[] parseCoordinates(JsonNode array) {
        Coordinate[] coords = new Coordinate[array.size()];
        for (int i = 0; i < array.size(); i++) {
            coords[i] = parseCoordinate(array.get(i));
        }
        return coords;
    }
}
