# Trial Day (Senior, Java, SpringBoot, ElasticSearch)

## Emergency Services Dashboard with Real-Time Search Capabilities

### Objective:

Develop a Java application using Spring Boot that acts as an emergency services dashboard. The application should
interact with ElasticSearch to log and query real-time emergency incidents.

### Implemented features:

1. endpoint `<host>/incidents/log` is available to create new incidents
2. endpoint `<host>/incidents/search` is available to search for incidents - sorting is done by timestamp descending
2. Unit-test for service (create & search incidents)
3. API-tests for `/incidents/log` and `/incidents/search` (positive & negative test cases)

### Missing features:

1. No serialization using Hibernate - all data goes directly into ES
2. No WebSocket integration
3. No UI
4. No dockerization


### Setup guide

1. Create a docker container running ES version 8.10.2 with default security settings:

```
    docker pull docker.elastic.co/elasticsearch/elasticsearch:8.10.2 
    docker run --name elasticsearch -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:8.10.2
```

2. When ES is running, create a new password for the user "elastic": 

```
    docker exec -it elasticsearch /usr/share/elasticsearch/bin/elasticsearch-reset-password -u elastic
```

3. Overwrite the value of the key `es.password` in the files `src/main/resources/application.properties` and `src/test/resources/application.properties` with the new password acquired in step 2
4. Copy the certificate file into `src/main/resources`: 

```
    docker cp elasticsearch:/usr/share/elasticsearch/config/certs/http_ca.crt <project root>/src/main/resources
```

5. Create the basic `incidents` index with the correct mapping:

```
curl --ssl-no-revoke --cacert http_ca.crt -u elastic:<password> -X PUT "https://localhost:9200/incidents" -H "Content-Type: application/json" -d' 
{
    "settings" : {
        "index" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 1
        }
    },
    "mappings": {
        "properties": {
            "incidentType": { "type": "keyword" },
            "location": { "type": "geo_point" },
            "timestamp": { "type": "date" },
            "severityLevel": { "type": "keyword" }
        }
    }
}'
```

6. Now test basic setup and functionality: `mvnw clean test`
6. When tests have finished successfully, the ES connection is working. Start the application: `mvnw spring-boot:run`
7. Now the endpoints `http://localhost:8080/incidents/logs` and `http://localhost:8080/incidents/search` are available
