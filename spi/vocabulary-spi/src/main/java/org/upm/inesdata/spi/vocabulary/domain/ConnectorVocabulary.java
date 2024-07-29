package org.upm.inesdata.spi.vocabulary.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;

@JsonDeserialize(builder = ConnectorVocabulary.Builder.class)
public class ConnectorVocabulary {

    public static final String PROPERTY_CONNECTOR_ID = EDC_NAMESPACE + "connectorId";

    private String connectorId;

    public ConnectorVocabulary() {
    }

    public String getConnectorId() {
        return connectorId;
    }

    public ConnectorVocabulary.Builder toBuilder() {
        return ConnectorVocabulary.Builder.newInstance()
                .connectorId(connectorId);
    }


    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        protected final ConnectorVocabulary connectorVocabulary;

        protected Builder(ConnectorVocabulary connectorVocabulary) {
            this.connectorVocabulary = connectorVocabulary;
        }

        @JsonCreator
        public static ConnectorVocabulary.Builder newInstance() {
            return new ConnectorVocabulary.Builder(new ConnectorVocabulary());
        }

        public ConnectorVocabulary.Builder connectorId(String connectorId) {
            connectorVocabulary.connectorId = connectorId;
            return self();
        }

        public ConnectorVocabulary.Builder self() {
            return this;
        }

        public ConnectorVocabulary build() {
            return connectorVocabulary;
        }
    }
}
