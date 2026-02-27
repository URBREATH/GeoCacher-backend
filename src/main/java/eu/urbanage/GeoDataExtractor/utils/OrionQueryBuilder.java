package eu.urbanage.GeoDataExtractor.utils;

import org.springframework.stereotype.Component;

@Component
public class OrionQueryBuilder {

    private StringBuilder urlBuilder;

    public OrionQueryBuilder(String baseURL) {
        this.urlBuilder = new StringBuilder(baseURL);
    }

    public OrionQueryBuilder(String baseURL, int limit) {

        String limitString = String.valueOf(limit);
        this.urlBuilder = new StringBuilder(baseURL + "/ngsi-ld/v1/entities?limit=" + limitString);
    }

    public OrionQueryBuilder() {
    }

    public OrionQueryBuilder addIdPattern(String type, String city) {
        String appendPattern = "&idPattern=urn:ngsi-ld:" + type + ":" + city + ":*";
        urlBuilder.append(appendPattern);
        return this;
    }

    public OrionQueryBuilder addIdPattern(String type, String city, String element) {
        String appendPattern = "&idPattern=urn:ngsi-ld:" + type + ":" + city + ":" + element + "*";
        urlBuilder.append(appendPattern);
        return this;
    }

    public OrionQueryBuilder addConcise() {
        urlBuilder.append("&options=concise");
        return this;
    }

    public OrionQueryBuilder addFilterQuery(String key, String value) {
        String appendQuery = "&q=" + key + "~=" + value;
        urlBuilder.append(appendQuery);
        return this;
    }

    // &georel=near;" + distanceType + ":" + innerPointRadius.getRadius() +
    // "&geometry=Point&options=concise&coords=" + innerPointRadius.getPointString()
    // +
    public OrionQueryBuilder addPointRadiusQuery(String distanceType, String radius, String pointString) {
        String appendQuery = "&georel=near;" + distanceType + ":" + radius + "&geometry=Point&options=concise&coords="
                + pointString;
        urlBuilder.append(appendQuery);
        return this;
    }

    // &georel=intersects&coords=" + innerPolygon.getPolygonString() +
    // "&geometry=Polygon
    public OrionQueryBuilder addPolygonQuery(String polygonString) {
        String appendQuery = "&georel=intersects&geometry=Polygon&coords=" + polygonString;
        urlBuilder.append(appendQuery);
        return this;
    }

    public OrionQueryBuilder addLocationQuery() {
        String appendQuery = "&attrs=location";
        urlBuilder.append(appendQuery);
        return this;
    }

    public OrionQueryBuilder addGeometryQuery() {
        String appendQuery = "&attrs=geometry";
        urlBuilder.append(appendQuery);
        return this;
    }

    public OrionQueryBuilder addQueryParam(String key, String value) {
        String appendQuery = "&" + key + "=" + value;
        urlBuilder.append(appendQuery);
        return this;
    }

    public String get() {
        return this.urlBuilder.toString();
    }
}
