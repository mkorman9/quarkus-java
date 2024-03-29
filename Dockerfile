FROM eclipse-temurin:17-jre

RUN addgroup --system --gid=9999 runner && \
    adduser --system --uid=9999 --gid=9999 --home /deployments --disabled-password runner

COPY --chown=runner:runner build/quarkus-app/lib/ /deployments/lib/
COPY --chown=runner:runner build/quarkus-app/*.jar /deployments/
COPY --chown=runner:runner build/quarkus-app/app/ /deployments/app/
COPY --chown=runner:runner build/quarkus-app/quarkus/ /deployments/quarkus/

USER runner
WORKDIR /

EXPOSE 8080
EXPOSE 3333
EXPOSE 7000

CMD [ "java", "-jar", "/deployments/quarkus-run.jar" ]
