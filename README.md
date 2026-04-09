# GeoCacher

**Provided by:** Engineering Ingegneria Informatica S.p.A. (ENG)

## Description

**GeoCacher** is a backend service designed to support a graphical dashboard for visualizing geospatial data. Its primary purpose is to interface with a [FIWARE ORION Context Broker](https://fiware-orion.readthedocs.io/en/master/) to extract and temporarily store (cache) data.

Key features include:
* **Data Extraction**: Retrieves filters and geospatial data from the ORION Context Broker.
* **Search Management**: Supports CRUD (Create, Read, Update, Delete) operations to save and manage user searches on a supporting MongoDB database.
* **GeoJSON Upload**: Allows users to upload and display custom GeoJSON files on the dashboard.

***

## Installation Prerequisites

To run this service, you must have the following installed on your system:
* [Docker](https://docs.docker.com/get-docker/)
* [Docker Compose](https://docs.docker.com/compose/install/)

***

## Installation Instructions

Follow these steps to run the GeoCacher service locally.

1.  **Clone the repository**
    Open a terminal and clone the repository to your computer.
    ```bash
    git clone https://github.com/URBREATH/GeoCacher-backend.git
    cd GeoCacher-backend
    ```

2.  **Build the Docker images**
    This command will build the service image and its supporting MongoDB database as defined in the `docker-compose.yml` file.
    ```bash
    docker-compose build
    ```

3.  **Start the containers**
    Run the containers in detached mode (in the background).
    ```bash
    docker-compose up -d
    ```
    The service will now be running.

***

## Built Image Registry

Details of the registry where the built image of this tool is stored are as follows.

* **Registry URL**: `registry.urbreath.tech`
* **Image Name**: `geocacher-be`
* **Version**: `0.0.1`

***

## External technical resources

* **APIs**: Once running locally, the API documentation is available via Swagger UI at: [http://127.0.0.1:9090/swagger-ui/index.html](http://127.0.0.1:9090/swagger-ui/index.html)
* **Orion Context Broker**: Official documentation for the FIWARE Context Broker. [https://fiware-orion.readthedocs.io/](https://fiware-orion.readthedocs.io/)
* **MongoDB**: Documentation for the NoSQL database used for search management. [https://docs.mongodb.com/](https://docs.mongodb.com/)

##  Dependencies and Contacts
|  |  |
|--------|---------|
| Dependencies | MongoDB, PostgreSQL, NGSI Broker, Orion, Geocacher frontend |
| Contacts | giovanniluca.dacierno@eng.it, rita.gaeta@eng.it |
| License | Proprietary |
