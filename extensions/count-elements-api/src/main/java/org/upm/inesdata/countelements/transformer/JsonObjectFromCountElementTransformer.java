package org.upm.inesdata.countelements.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import org.eclipse.edc.jsonld.spi.transformer.AbstractJsonLdTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.upm.inesdata.spi.countelements.domain.CountElement;

import static org.upm.inesdata.spi.countelements.domain.CountElement.PROPERTY_COUNT;


/**
 * Creates a JsonObject from a {@link CountElement}
 */
public class JsonObjectFromCountElementTransformer extends AbstractJsonLdTransformer<CountElement, JsonObject> {
    private final ObjectMapper mapper;
    private final JsonBuilderFactory jsonFactory;

    /**
     * Constructor
     */
    public JsonObjectFromCountElementTransformer(JsonBuilderFactory jsonFactory, ObjectMapper jsonLdMapper) {
        super(CountElement.class, JsonObject.class);
        this.jsonFactory = jsonFactory;
        this.mapper = jsonLdMapper;
    }

    @Override
    public @Nullable JsonObject transform(@NotNull CountElement countElement, @NotNull TransformerContext context) {
        var builder = jsonFactory.createObjectBuilder()
                .add(PROPERTY_COUNT, countElement.getCount());

        return builder.build();
    }
}
