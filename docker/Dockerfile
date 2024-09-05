FROM eclipse-temurin:17-jre-jammy
ARG CONNECTOR_JAR

# System env vars
ENV USER_GROUP=connectorgroup
ENV USER_NAME=connectoruser
ENV JVM_ARGS=""
ENV CONNECTOR_HOME=/opt/connector
ENV CONNECTOR_RESOURCES=${CONNECTOR_HOME}/resources

# Add the user who executes the connector. The user belongs to the group with gui 0
RUN addgroup ${USER_GROUP} && adduser --no-create-home --disabled-password --ingroup ${USER_GROUP} ${USER_NAME} 

# Create the connector and resources directories
RUN mkdir -p ${CONNECTOR_HOME}
RUN mkdir -p ${CONNECTOR_RESOURCES}

# Work with the minimum rights
RUN chown -R ${USER_NAME}:0 ${CONNECTOR_HOME} \
    && chmod -R g=u ${CONNECTOR_HOME}

# Helpful utility while the connector is not able to create its own database
RUN apt-get update && apt-get install -y postgresql-client

# User by default
USER ${USER_NAME} 

# Set the workdir
WORKDIR ${CONNECTOR_HOME}

# Copy connector
COPY $CONNECTOR_JAR connector.jar
COPY resources/* ${CONNECTOR_RESOURCES}/


# Use "exec" for Kubernetes graceful termination (SIGINT) to reach JVM.
ENTRYPOINT [ "sh", "-c", \
    "exec java $JVM_ARGS -jar connector.jar"]
