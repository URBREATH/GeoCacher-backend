# GeoCacher

**Provided by:** Engineering Ingegneria Informatica S.p.A. (ENG)

## Description

**GeoCacher** is a backend service designed to support a graphical dashboard for visualizing geospatial data. It interfaces with a [FIWARE ORION Context Broker](https://fiware-orion.readthedocs.io/en/master/) to extract and cache geospatial data, and integrates with [GeoServer](https://geoserver.org/) for publishing and serving map layers.

Key features:

* **Data Extraction** — Retrieves geospatial data from the ORION Context Broker using NGSI-LD queries (multi-polygon and multi-point-radius geometries).
* **Search Management** — CRUD operations for saving and managing user searches in MongoDB.
* **GeoJSON / Layer Management** — Upload GeoJSON files, store features in PostGIS, publish and manage layers on GeoServer.
* **Scheduled Refresh** — Quartz-based scheduler that automatically refreshes cached geospatial data for each saved user search.
* **IDRA Integration** — Publishes datasets and distributions to an NGSI Broker (IDRA).
* **Authentication** — Keycloak OAuth2/OIDC integration for user identity and access management.

---

## Architecture

```
Frontend ──► GeoCacher Backend (Spring Boot, port 9090)
                 │
                 ├──► MongoDB              (document / search storage)
                 ├──► PostgreSQL + PostGIS  (geospatial feature storage)
                 ├──► GeoServer            (map layer publishing)
                 ├──► ORION Context Broker  (NGSI-LD data source)
                 ├──► NGSI Broker / IDRA   (dataset publishing)
                 └──► Keycloak             (authentication)
```

---

## Tech Stack

| Component | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.1 |
| Document DB | MongoDB 5.0 |
| Spatial DB | PostgreSQL 15 + PostGIS 3.3 |
| Map server | GeoServer (external) |
| Auth | Keycloak (external) |
| Scheduler | Quartz |
| Container | Docker / Docker Compose |

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

The `docker-compose.yml` includes defaults for a local development setup. For production, override the following variables (either in `docker-compose.yml` or as host environment variables):

| Variable | Description | Default |
|---|---|---|
| `SERVER_URL` | Public base URL of this service | `https://geocacher-api-dev.urbreath.tech` |
| `HOST_ORION` | ORION Context Broker base URL | `https://orion-dev.urbreath.tech` |
| `HOST_NGSIBROKER` | NGSI Broker / IDRA base URL | `https://ngsi-broker-dev.urbreath.tech` |
| `MONGODB_URL` | MongoDB connection string | `mongodb://mongo` |
| `GEOSERVER_URL` | GeoServer base URL | `http://geoserver-dev.urbreath.tech/geoserver` |
| `GEOSERVER_ADMIN` | GeoServer admin username | `admin` |
| `GEOSERVER_PASSWORD` | GeoServer admin password | `geoserver` |
| `POSTGRES_URL` | PostGIS JDBC URL | `jdbc:postgresql://postgis:5432/ProvaPostGIS` |
| `POSTGRES_USER` | PostGIS username | `postgres` |
| `POSTGRES_PASSWORD` | PostGIS password | `postgres` |
| `POSTGIS_HOST` | PostGIS hostname (used by GeoServer datastore config) | `postgis` |
| `POSTGIS_PORT` | PostGIS port | `5432` |
| `POSTGIS_DB` | PostGIS database name | `ProvaPostGIS` |
| `KEYCLOAK_URL` | Keycloak auth server URL | `http://keycloak:8080/auth` |
| `KEYCLOAK_REALM` | Keycloak realm name | `urbreath-auth` |
| `KEYCLOAK_CLIENT_ID` | Keycloak client ID | `my-client` |
| `KEYCLOAK_CLIENT_SECRET` | Keycloak client secret | `my-secret` |
| `KEYCLOAK_REDIRECT_URI` | OAuth2 redirect URI after login | `http://localhost:4200/login` |
| `APP_CITIES` | Comma-separated list of cities for filter discovery | `Aarhus,Athens,Cluj-Napoca,Kajaani,Leuven,Madrid,Parma,Pilsen,Tallinn` |

> **Note:** GeoServer and Keycloak are **not** included in the `docker-compose.yml` and must be provisioned separately.

### 3. Build and start

```bash
docker-compose build
docker-compose up -d
```

The stack starts MongoDB, PostGIS, and the backend on port `9090`.

---

## API Reference

The full interactive API documentation is available via Swagger UI once the service is running:

**[http://127.0.0.1:9090/swagger-ui/index.html](http://127.0.0.1:9090/swagger-ui/index.html)**

### Authentication — `/api/auth`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/auth/login-url` | Returns the Keycloak login URL |
| `POST` | `/api/auth/token?code=` | Exchanges an authorization code for a token |

### Documents — `/api/document`

All write endpoints require a valid `Authorization: Bearer <token>` header.

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/document/save/` | Create a new document (search) |
| `POST` | `/api/document/update/` | Update an existing document |
| `GET` | `/api/document/{id}` | Get a document by ID |
| `DELETE` | `/api/document/{id}` | Delete a document and its associated cron |
| `GET` | `/api/document/getdocuments` | Get all documents for the authenticated user |
| `GET` | `/api/document/getalldocuments` | Get all documents |
| `GET` | `/api/document/getalldocuments/{city}` | Get all documents for a city |
| `GET` | `/api/document/getGeojson/{id}` | Get the cached GeoJSON for a document |

### Scheduled Jobs (Cron) — `/api/cron`

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/cron/set/` | Create a new cron job for a document |
| `POST` | `/api/cron/update/` | Update an existing cron job |
| `GET` | `/api/cron/{id}` | Get a cron job by ID |
| `DELETE` | `/api/cron/{id}` | Delete a cron job |

### Geospatial Data — ORION Queries

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/multipolygondata/` | Query ORION data within multiple polygons |
| `POST` | `/api/multipointradiusdata/` | Query ORION data within multiple point-radius areas |

### Filters — `/api/filter`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/filter/{cityName}` | Get available entity type filters for a city |

### GeoServer — `/api/geoserver`

#### Workspaces

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/geoserver/workspaces?workspace=` | Create a new workspace |
| `GET` | `/api/geoserver/workspaces` | List all workspaces |
| `DELETE` | `/api/geoserver/workspaces/{workspaceName}` | Delete a workspace (recursive) |

#### Datastores

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/geoserver/datastores?workspace=&datastore=` | Create a PostGIS datastore |
| `GET` | `/api/geoserver/datastores?workspace=` | List datastores in a workspace |
| `DELETE` | `/api/geoserver/datastores/{datastoreName}?workspace=` | Delete a datastore |

#### Layers

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/geoserver/layers?workspace=&datastore=&layer=` | Create PostGIS table and publish layer on GeoServer |
| `GET` | `/api/geoserver/layers?workspace=&datastore=` | List layers in a datastore |
| `GET` | `/api/geoserver/layers/{workspace}/{layerName}` | Get layer features as GeoJSON |
| `DELETE` | `/api/geoserver/layers/{layerName}?workspace=&datastore=` | Delete layer from GeoServer and drop PostGIS table |

#### Features

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/geoserver/features?layer=` | Save GeoJSON features into a PostGIS table |
| `GET` | `/api/geoserver/features/{id}?workspace=&layerName=` | Get a feature by ID |
| `PUT` | `/api/geoserver/features/{id}?layerName=` | Update a feature by ID |
| `DELETE` | `/api/geoserver/features/{id}?layerName=` | Delete a feature by ID |
| `DELETE` | `/api/geoserver/features?layerName=` | Delete all features from a table |

### IDRA — `/api/idra`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/idra/{id}` | Publish a document as a dataset/distribution on IDRA |

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
* [Quartz Scheduler](http://www.quartz-scheduler.org/documentation/) — Job scheduling
