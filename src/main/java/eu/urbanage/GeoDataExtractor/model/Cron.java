package eu.urbanage.GeoDataExtractor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

public class Cron {

    @Id
    @JsonProperty()
    private String id;

    @JsonProperty("document_id")
    private String documentID;

    private String city;

    private List<String> filter;

    private Date data_created;

    private Date data_last_execution;

    @JsonProperty("repeat")
    private int repeat;

    @JsonProperty("multiPolygon")
    private List<List<Coordinates>> multiPolygon;

    @JsonProperty("multipoint")
    private List<PointRadiusFeature> multipoint;

    public Cron() {

        this.data_created = new Date();

        this.data_last_execution = new Date();

    }

    public Date getData_created() {
        return data_created;
    }

    public void setData_created(Date data_created) {
        this.data_created = data_created;
    }

    public Date getData_last_execution() {
        return data_last_execution;
    }

    public void setData_last_execution(Date data_last_execution) {
        this.data_last_execution = data_last_execution;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocument_id() {
        return documentID;
    }

    public void setDocument_id(String document_id) {
        this.documentID = document_id;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public List<List<Coordinates>> getMultiPolygon() {
        return multiPolygon;
    }

    public void setMultiPolygon(List<List<Coordinates>> multiPolygon) {
        this.multiPolygon = multiPolygon;
    }

    public List<PointRadiusFeature> getMultipoint() {
        return multipoint;
    }

    public void setMultipoint(List<PointRadiusFeature> multipoint) {
        this.multipoint = multipoint;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(List<String> filter) {
        this.filter = filter;
    }

    public Object getCron_expression() {
        throw new UnsupportedOperationException("Unimplemented method 'getCron_expression'");
    }
}
