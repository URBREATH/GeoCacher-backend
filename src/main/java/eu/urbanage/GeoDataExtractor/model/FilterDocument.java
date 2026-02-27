package eu.urbanage.GeoDataExtractor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

public class FilterDocument {
    @Id
    @JsonProperty()
    private String id;

    @JsonProperty("city")
    private String cityName;

    @JsonProperty("filter")
    private List<String> filter;

    private List<String> detailFilter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(List<String> filter) {
        this.filter = filter;
    }

    public List<String> getDetailFilter() {
        return detailFilter;
    }


    public void setDetailFilter(List<String> detailFilter) {
        this.detailFilter = detailFilter;
    }

}
