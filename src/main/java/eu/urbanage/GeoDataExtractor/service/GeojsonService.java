package eu.urbanage.GeoDataExtractor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.urbanage.GeoDataExtractor.model.*;
import eu.urbanage.GeoDataExtractor.utils.OrionQueryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GeojsonService implements GeojsonClient {

    RestTemplate restTemplate = new RestTemplate();

    @Value("${HOST_ORION}")
    private String hostContextBroker;

    public GeojsonService(@Value("${HOST_ORION}") String hostContextBroker) {
        this.hostContextBroker = hostContextBroker;
    }

    @Override
    public List<String> getFromMultiPolygon(MultiPolygon data) {

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Accept", "application/geo+json");
        headers.add("Content-Type", "application/json");
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Headers", "*");
        headers.add("Access-Control-Allow-Methods", "*");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        List<String> GeoData = new ArrayList<String>();

        ResponseEntity<String> response;
        // TODO: handle limit - offset

        List<String> filter_list = data.getFilter();
        List<List<String>> sub_filter_list = data.getSubfilter();

        List<List<Coordinates>> multiPolygon = data.getMultiPolygon();

        for (List<Coordinates> polygon : multiPolygon) {

            Polygon innerPolygon = new Polygon(polygon, data.getFilter(), data.getCityName());

            for (String filter : filter_list) {

                if (sub_filter_list != null) {
                    OrionQueryBuilder oqb = new OrionQueryBuilder(hostContextBroker, 1000);

                    for (List<String> sub_filter : sub_filter_list) {

                        String sub_key = sub_filter.get(0);
                        String sub_value = sub_filter.get(1);

                        if (sub_key.equals("id_category")) {

                            oqb.addIdPattern(filter, data.getCityName(), sub_value);

                        } else {

                            oqb.addIdPattern(filter, data.getCityName()).addFilterQuery(sub_key, sub_value);
                        }

                        String url = oqb.addConcise().addPolygonQuery(innerPolygon.getPolygonString()).get();

                        System.out.println(url);

                        response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
                        List<String> responseArr = Arrays.asList(response.getBody());
                        GeoData.addAll(responseArr);

                    }

                } else {
                    OrionQueryBuilder oqb = new OrionQueryBuilder(hostContextBroker, 1000);
                    String url = oqb.addIdPattern(filter, innerPolygon.getCityName())
                            .addPolygonQuery(innerPolygon.getPolygonString()).addConcise().get();
                    response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

                    List<String> responseArr = Arrays.asList(response.getBody());
                    GeoData.addAll(responseArr);
                }
            }
        }

        return GeoData;
    }

    @Override
    public List<String> getFromMultiPointRadius(MultiPointRadius data) throws JsonProcessingException {

        String distanceType;

        // TODO: fix this
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Accept", "application/geo+json");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        List<String> GeoData = new ArrayList<String>();

        ResponseEntity<String> response;
        // TODO: handle limit - offset

        List<String> filter_list = data.getFilter();
        List<PointRadiusFeature> multiPoint = data.getMultipoint();
        List<List<String>> sub_filter_list = data.getSubfilter();

        for (PointRadiusFeature pointRadius : multiPoint) {

            PointRadius innerPointRadius = new PointRadius(pointRadius.getPoint(), pointRadius.getRadius(),
                    data.getFilter(), data.getCityName(), pointRadius.isExternal());

            if (innerPointRadius.isExternal()) {
                distanceType = "minDistance";
            } else {
                distanceType = "maxDistance";
            }

            for (String filter : filter_list) {
                if (sub_filter_list != null) {
                    OrionQueryBuilder oqb = new OrionQueryBuilder(hostContextBroker, 1000);

                    for (List<String> sub_filter : sub_filter_list) {

                        String sub_key = sub_filter.get(0);
                        String sub_value = sub_filter.get(1);

                        if (sub_key.equals("id_category")) {

                            oqb.addIdPattern(filter, data.getCityName(), sub_value);

                        } else {

                            oqb.addIdPattern(filter, data.getCityName()).addFilterQuery(sub_key, sub_value);
                        }

                        String url = oqb.addConcise().addPointRadiusQuery(distanceType, innerPointRadius.getRadius(),
                                innerPointRadius.getPointString()).get();

                        response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
                        List<String> responseArr = Arrays.asList(response.getBody());
                        GeoData.addAll(responseArr);

                    }

                } else {

                    OrionQueryBuilder oqb = new OrionQueryBuilder(hostContextBroker, 1000);

                    String url = oqb.addIdPattern(filter, innerPointRadius.getCityName())
                            .addPointRadiusQuery(distanceType, innerPointRadius.getRadius(),
                                    innerPointRadius.getPointString())
                            .addConcise().get();
                    response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
                    List<String> responseArr = Arrays.asList(response.getBody());
                    GeoData.addAll(responseArr);
                }
            }

        }

        return GeoData;
        // return Arrays.asList(response.getBody());
    }

}
