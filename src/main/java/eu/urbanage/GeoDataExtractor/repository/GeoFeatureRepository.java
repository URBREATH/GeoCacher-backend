package eu.urbanage.GeoDataExtractor.repository;

import eu.urbanage.GeoDataExtractor.entity.GeoFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoFeatureRepository extends JpaRepository<GeoFeature, Long> {



}
