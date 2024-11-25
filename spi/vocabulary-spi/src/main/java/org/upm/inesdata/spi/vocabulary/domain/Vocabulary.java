package org.upm.inesdata.spi.vocabulary.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.spi.entity.Entity;

import java.util.Objects;
import java.util.UUID;

import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;


/**
 * The {@link Vocabulary} contains the vocabylary information.
 */
@JsonDeserialize(builder = Vocabulary.Builder.class)
public class Vocabulary extends Entity {

    public static final String PROPERTY_ID = EDC_NAMESPACE + "id";
    public static final String PROPERTY_NAME = EDC_NAMESPACE + "name";
    public static final String PROPERTY_JSON_SCHEMA = EDC_NAMESPACE + "jsonSchema";
    public static final String PROPERTY_CATEGORY = EDC_NAMESPACE + "category";
    public static final String PROPERTY_CONNECTOR_ID = EDC_NAMESPACE + "connectorId";
    public static final String EDC_VOCABULARY_TYPE = EDC_NAMESPACE + "Vocabulary";
    

    private String name;
    private String jsonSchema;
    private String category;
    private String connectorId;

    private Vocabulary() {
    }

    public String getName() {
        return name;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    public String getCategory() {
        return category;
    }
    public String getConnectorId() {
        return connectorId;
    }


    public Builder toBuilder() {
        return Vocabulary.Builder.newInstance()
                .id(id)
                .name(name)
                .connectorId(connectorId)
                .jsonSchema(jsonSchema)
                .category(category)
                .createdAt(createdAt);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends Entity.Builder<Vocabulary, Builder> {

        protected Builder(Vocabulary vocabulary) {
            super(vocabulary);
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder(new Vocabulary());
        }

        @Override
        public Builder id(String id) {
            entity.id = id;
            return self();
        }

        public Builder name(String name) {
            entity.name = name;
            return self();
        }

        public Builder connectorId(String connectorId) {
            entity.connectorId = connectorId;
            return self();
        }

        public Builder jsonSchema(String jsonSchema) {
            Objects.requireNonNull(jsonSchema);
            entity.jsonSchema = jsonSchema;
            return self();
        }

        public Builder category(String category) {
            entity.category = category;
            return self();
        }

        @Override
        public Builder createdAt(long value) {
            entity.createdAt = value;
            return self();
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public Vocabulary build() {
            super.build();

            if (entity.getId() == null) {
                id(UUID.randomUUID().toString());
            }

            return entity;
        }
    }

}
