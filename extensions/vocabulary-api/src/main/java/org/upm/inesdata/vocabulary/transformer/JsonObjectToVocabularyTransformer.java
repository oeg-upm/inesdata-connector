package org.upm.inesdata.vocabulary.transformer;

import jakarta.json.JsonObject;
import org.eclipse.edc.jsonld.spi.transformer.AbstractJsonLdTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;

import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_JSON_SCHEMA;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_NAME;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_CATEGORY;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_DEFAULT_VOCABULARY;

/**
 * Converts from an {@link Vocabulary} as a {@link JsonObject} in JSON-LD expanded form to an {@link Vocabulary}.
 */
public class JsonObjectToVocabularyTransformer extends AbstractJsonLdTransformer<JsonObject, Vocabulary> {
    
    /**
     * Constructor
     */
    public JsonObjectToVocabularyTransformer() {
        super(JsonObject.class, Vocabulary.class);
    }

    @Override
    public @Nullable Vocabulary transform(@NotNull JsonObject jsonObject, @NotNull TransformerContext context) {
        var builder = Vocabulary.Builder.newInstance()
                .id(nodeId(jsonObject));

        visitProperties(jsonObject, key -> switch (key) {
            case PROPERTY_NAME -> value -> builder.name(transformString(value, context));
            case PROPERTY_JSON_SCHEMA -> value -> builder.jsonSchema(transformString(value, context));
            case PROPERTY_CATEGORY -> value -> builder.category(transformString(value, context));
            case PROPERTY_DEFAULT_VOCABULARY -> value -> builder.defaultVocabulary(transformBoolean(value, context));
            default -> doNothing();
        });

        if (!jsonObject.containsKey(PROPERTY_DEFAULT_VOCABULARY)) {
            builder.defaultVocabulary(false);
        }

        return builderResult(builder::build, context);
    }

}
