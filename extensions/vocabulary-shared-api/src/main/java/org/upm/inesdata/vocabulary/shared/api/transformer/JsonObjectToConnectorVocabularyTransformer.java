package org.upm.inesdata.vocabulary.shared.api.transformer;

import jakarta.json.JsonObject;
import org.eclipse.edc.jsonld.spi.transformer.AbstractJsonLdTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.upm.inesdata.spi.vocabulary.domain.ConnectorVocabulary;

import static org.upm.inesdata.spi.vocabulary.domain.ConnectorVocabulary.PROPERTY_CONNECTOR_ID;

/**
 * Converts from an {@link ConnectorVocabulary} as a {@link JsonObject} in JSON-LD expanded form to an {@link ConnectorVocabulary}.
 */
public class JsonObjectToConnectorVocabularyTransformer extends AbstractJsonLdTransformer<JsonObject, ConnectorVocabulary> {
    /**
     * Constructor
     */
    public JsonObjectToConnectorVocabularyTransformer() {
        super(JsonObject.class, ConnectorVocabulary.class);
    }

    @Override
    public @Nullable ConnectorVocabulary transform(@NotNull JsonObject jsonObject, @NotNull TransformerContext context) {
        var builder = ConnectorVocabulary.Builder.newInstance();

        visitProperties(jsonObject, key -> value -> {
            if (PROPERTY_CONNECTOR_ID.equals(key)) {
                builder.connectorId(transformString(value, context));
            }
        });

        return builderResult(builder::build, context);
    }
}
