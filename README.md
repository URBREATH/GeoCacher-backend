# GeoCacher

**Provided by:** Engineering Ingegneria Informatica S.p.A. (ENG)

## Description

**GeoCacher** is a backend service designed to support a graphical dashboard for visualizing geospatial data. It interfaces with a [FIWARE ORION Context Broker](https://fiware-orion.readthedocs.io/en/master/) to extract and cache geospatial data, and integrates with [GeoServer](https://geoserver.org/) for publishing and serving map layers.

Key features include:

* **Data Extraction**: Retrieves filters and geospatial data from the ORION Context Broker using NGSI-LD queries (polygon, point-radius).
* **Search Management**: CRUD operations for saving and managing user searches in MongoDB.
* **GeoJSON Management**: Upload custom GeoJSON files, store features in PostGIS, and publish them as GeoServer layers.
* **Scheduled Jobs**: Quartz-based scheduler that automatically refreshes cached geospatial data for each saved user search.
* **IDRA Integration**: Publishes datasets and distributions to an NGSI Broker (IDRA).
* **Authentication**: Keycloak OAuth2/OIDC integration for user identity and access management.

---

## Architecture

```
Frontend ──► GeoCacher Backend (Spring Boot, port 9090)
                 │
                 ├──► MongoDB          (document/search storage)
                 ├──► PostgreSQL+PostGIS (geospatial feature storage)
                 ├──► GeoServer         (map layer publishing)
                 ├──► ORION Context Broker (NGSI-LD data source)
                 ├──► NGSI Broker / IDRA  (dataset publishing)
                 └──► Keycloak           (authentication)
```

---

## Installation Prerequisites

* [Docker](https://docs.docker.com/get-docker/)
* [Docker Compose](https://docs.docker.com/compose/install/)

---

## Installation Instructions

### 1. Clone the repository

```bash
git clone https://github.com/URBREATH/GeoCacher-backend.git
cd GeoCacher-backend
```

### 2. Configure environment variables

The `docker-compose.yml` already includes sensible defaults for a local development setup. For a production deployment, override the following variables:

| Variable | Description | Default |
|---|---|---|
| `SERVER_URL` | Public base URL of this service | `https://geocacher-api-dev.urbreath.tech` |
| `HOST_ORION` | ORION Context Broker URL | `https://orion-dev.urbreath.tech` |
| `HOST_NGSIBROKER` | NGSI Broker / IDRA URL | `https://ngsi-broker-dev.urbreath.tech` |
| `MONGODB_URL` | MongoDB connection string | `mongodb://mongo` |
| `GEOSERVER_URL` | GeoServer base URL | `http://geoserver-dev.urbreath.tech/geoserver` |
| `GEOSERVER_ADMIN` | GeoServer admin username | `admin` |
| `GEOSERVER_PASSWORD` | GeoServer admin password | `geoserver` |
| `POSTGRES_URL` | PostGIS JDBC URL | `jdbc:postgresql://postgis:5432/ProvaPostGIS` |
| `POSTGRES_USER` | PostGIS username | `postgres` |
| `POSTGRES_PASSWORD` | PostGIS password | `postgres` |
| `POSTGIS_HOST` | PostGIS hostname (for GeoServer datastore) | `postgis` |
| `POSTGIS_PORT` | PostGIS port | `5432` |
| `POSTGIS_DB` | PostGIS database name | `ProvaPostGIS` |
| `KEYCLOAK_URL` | Keycloak auth server URL | `http://keycloak:8080/auth` |
| `KEYCLOAK_REALM` | Keycloak realm name | `urbreath-auth` |
| `KEYCLOAK_CLIENT_ID` | Keycloak client ID | `my-client` |
| `KEYCLOAK_CLIENT_SECRET` | Keycloak client secret | `my-secret` |
| `KEYCLOAK_REDIRECT_URI` | OAuth2 redirect URI after login | `http://localhost:4200/login` |
| `APP_CITIES` | Comma-separated list of cities for filter discovery | `Aarhus,Athens,...` |

### 3. Build and start

```bash
docker-compose build
docker-compose up -d
```

The service starts on port `9090`. The stack includes MongoDB, PostGIS, and the backend itself (GeoServer must be provided externally).

---

## API Documentation

Once running, the full API reference is available via Swagger UI:

[http://127.0.0.1:9090/swagger-ui/index.html](http://127.0.0.1:9090/swagger-ui/index.html)

---

## Built Image Registry

| Field | Value |
|---|---|
| Registry URL | `registry.urbreath.tech` |
| Image Name | `geocacher-be` |
| Version | `0.0.1` |

---

## External Resources

* [FIWARE ORION Context Broker](https://fiware-orion.readthedocs.io/) — NGSI-LD data source
* [GeoServer](https://docs.geoserver.org/) — Map layer publishing
* [MongoDB](https://docs.mongodb.com/) — Document storage
* [PostGIS](https://postgis.net/documentation/) — Geospatial feature storage
* [Keycloak](https://www.keycloak.org/documentation) — Authentication and authorization
