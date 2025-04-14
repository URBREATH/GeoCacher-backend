package eu.urbanage.GeoDataExtractor.entity;

import jakarta.persistence.*;

import org.locationtech.jts.geom.Geometry;


@Entity
@Table(name = "geojson_features")
public class GeoFeature {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR")
    private String properties;

    @Column(columnDefinition = "geometry(Geometry,4326)")
    private Geometry geometry;

    public Long getId() {
        return id;
    }

    public String getProperties() {
        return properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
