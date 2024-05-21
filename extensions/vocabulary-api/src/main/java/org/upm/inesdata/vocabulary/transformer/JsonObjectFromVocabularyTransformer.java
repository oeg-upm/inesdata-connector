package org.upm.inesdata.vocabulary.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;

import org.eclipse.edc.jsonld.spi.transformer.AbstractJsonLdTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;

import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;

import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.EDC_VOCABULARY_TYPE;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_JSON_SCHEMA;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_NAME;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_CATEGORY;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_DEFAULT_VOCABULARY;


/**
 * Creates a JsonObject from a {@link Vocabulary} 
 */
public class JsonObjectFromVocabularyTransformer extends AbstractJsonLdTransformer<Vocabulary, JsonObject> {
    private final ObjectMapper mapper;
    private final JsonBuilderFactory jsonFactory;

    /**
     * Constructor
     */
    public JsonObjectFromVocabularyTransformer(JsonBuilderFactory jsonFactory, ObjectMapper jsonLdMapper) {
        super(Vocabulary.class, JsonObject.class);
        this.jsonFactory = jsonFactory;
        this.mapper = jsonLdMapper;
    }

    @Override
    public @Nullable JsonObject transform(@NotNull Vocabulary vocabulary, @NotNull TransformerContext context) {
        var builder = jsonFactory.createObjectBuilder()
                .add(ID, vocabulary.getId())
                .add(TYPE, EDC_VOCABULARY_TYPE)
                .add(PROPERTY_NAME, vocabulary.getName())
                .add(PROPERTY_JSON_SCHEMA, vocabulary.getJsonSchema())
                .add(PROPERTY_CATEGORY, vocabulary.getCategory())
                .add(PROPERTY_DEFAULT_VOCABULARY, vocabulary.isDefaultVocabulary());

        return builder.build();
    }
}
