CREATE EXTENSION IF NOT EXISTS postgis;

DROP TABLE IF EXISTS geojson_features;

CREATE TABLE geojson_features (
    id SERIAL PRIMARY KEY,
    geometry geometry(Geometry, 4326), 
    properties VARCHAR
);

CREATE INDEX IF NOT EXISTS idx_geojson_features_geom
    ON geojson_features
    USING GIST (geometry);