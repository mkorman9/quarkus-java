FROM eclipse-temurin:17-alpine

RUN adduser -u 9999 -D -H runner

COPY --chown=9999 build/quarkus-app/lib/ /deployments/lib/
COPY --chown=9999 build/quarkus-app/*.jar /deployments/
COPY --chown=9999 build/quarkus-app/app/ /deployments/app/
COPY --chown=9999 build/quarkus-app/quarkus/ /deployments/quarkus/

USER runner

CMD [ "java", "-jar", "/deployments/quarkus-run.jar" ]
