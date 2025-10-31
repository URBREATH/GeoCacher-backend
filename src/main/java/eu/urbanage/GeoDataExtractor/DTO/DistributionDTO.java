package eu.urbanage.GeoDataExtractor.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistributionDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("downloadURL")
    private String downloadURL;

    @JsonProperty("format")
    private String format;

    public DistributionDTO(String id, String title, String description, String downloadURL) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.downloadURL = downloadURL;
        this.format = "geojson";
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }
}
