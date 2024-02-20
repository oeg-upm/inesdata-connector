FROM openjdk:17-jdk-alpine
ARG CONNECTOR_JAR

# System env vars
ENV USER_NAME=connectoruser
ENV USER_UID=200
ENV JVM_ARGS=""
ENV CONNECTOR_HOME=/opt/connector
ENV CONNECTOR_RESOURCES=${CONNECTOR_HOME}/resources

# Add the user who executes the connector. The user belongs to the group with gui 0
RUN adduser -G root -s /bin/sh -D -u ${USER_UID} ${USER_NAME}

# Create the connector and resources directories
RUN mkdir -p ${CONNECTOR_HOME}
RUN mkdir -p ${CONNECTOR_RESOURCES}

# Work with the minimum rights
RUN chown -R ${USER_NAME}:0 ${CONNECTOR_HOME} \
    && chmod -R g=u ${CONNECTOR_HOME}

# User by default
USER ${USER_NAME} 

# Set the workdir
WORKDIR ${CONNECTOR_HOME}

# Copy connector
COPY $CONNECTOR_JAR connector.jar

# Use "exec" for Kubernetes graceful termination (SIGINT) to reach JVM.
ENTRYPOINT [ "sh", "-c", \
    "exec java $JVM_ARGS -jar connector.jar"]
