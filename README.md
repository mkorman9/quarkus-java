# quarkus-java

## Build

NOTE: Building requires JDK 17
     
```shell
./gradlew build

docker build -t quarkus-java .
```

## Test

(Requires running Docker Daemon)
```shell
./gradlew integrationTest
```

## Deploy to Docker Swarm

Create secrets file `secrets.properties`
```properties
quarkus.datasource.jdbc.url=jdbc:postgresql://prod.db.example.com:5432/quarkus-java
quarkus.datasource.username=app
quarkus.datasource.password=password

oauth2.github.clientId=01234567890
oauth2.github.clientSecret=01234567890

jwt.secret=0123456789012345678901234567890
```

Create secret
```shell
cat secrets.properties | docker secret create app_properties -
```

Deploy
```shell
docker service create \
  --name quarkus-java \
  --replicas 1 \
  --publish published=8080,target=8080 \
  --secret src=app_properties,target="/config/application.properties" \
  quarkus-java:latest
```
