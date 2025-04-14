DROP TABLE IF EXISTS geojson_features;

CREATE TABLE geojson_features (
    id SERIAL PRIMARY KEY,
    geometry geometry(Geometry, 4326),
    properties jsonb
);
