FROM eclipse-temurin:17

COPY --chown=185 build/quarkus-app/lib/ /deployments/lib/
COPY --chown=185 build/quarkus-app/*.jar /deployments/
COPY --chown=185 build/quarkus-app/app/ /deployments/app/
COPY --chown=185 build/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080

CMD [ "java",                                                        \
      "-Dquarkus.http.host=0.0.0.0",                                 \
      "-Djava.util.logging.manager=org.jboss.logmanager.LogManager", \
      "-jar",                                                        \
      "/deployments/quarkus-run.jar"                                 \
]
