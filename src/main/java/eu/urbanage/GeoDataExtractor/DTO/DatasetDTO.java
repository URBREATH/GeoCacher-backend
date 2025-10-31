package eu.urbanage.GeoDataExtractor.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class DatasetDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("datasetDescription")
    private List<String> datasetDescription;

    @JsonProperty("description")
    private String description;

    @JsonProperty("datasetDistribution")
    private List<String> datasetDistribution;

    public DatasetDTO(String id, String title, String description, String distribId) {

        this.id = id;
        this.title = title;

        List<String> distribIdOBJ = new ArrayList<String>();
        List<String> distribdescrOBJ = new ArrayList<String>();

        distribIdOBJ.add(distribId);
        distribdescrOBJ.add(description);

        this.datasetDescription = distribdescrOBJ;
        this.description = description;
        this.datasetDistribution = distribIdOBJ;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getDatasetDescription() {
        return datasetDescription;
    }

    public void setDatasetDescription(List<String> datasetDescription) {
        this.datasetDescription = datasetDescription;
    }

    public List<String> getDatasetDistribution() {
        return datasetDistribution;
    }

    public void setDatasetDistribution(List<String> datasetDistribution) {
        this.datasetDistribution = datasetDistribution;
    }
}
