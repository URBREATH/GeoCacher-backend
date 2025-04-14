package eu.urbanage.GeoDataExtractor.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeoServerQueryBuilder {

    private StringBuilder urlBuilder;
    private static final String WFS_URL = "/ows?service=WFS&version=2.0.0";

    public GeoServerQueryBuilder(@Value("${geoserver.url}")String baseURL) {
        this.urlBuilder = new StringBuilder(baseURL + WFS_URL);
    }


    public GeoServerQueryBuilder setTypeName(String layerName) {
        urlBuilder.append("&typeName=").append(layerName);
        return this;
    }

    public GeoServerQueryBuilder setGetFeatureRequest() {
        urlBuilder.append("&request=GetFeature");
        return this;
    }

    public GeoServerQueryBuilder setTransactionRequest() {
        urlBuilder.append("&request=Transaction");
        return this;
    }


    public GeoServerQueryBuilder setOutputFormat(String format) {
        urlBuilder.append("&outputFormat=").append(format);
        return this;
    }

    public String build() {
        return urlBuilder.toString();
    }

}
