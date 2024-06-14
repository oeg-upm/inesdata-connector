package org.upm.inesdata.spi.countelements.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.spi.entity.Entity;

/**
 * The {@link CountElement} of elements of an entity
 */
@JsonDeserialize(builder = CountElement.Builder.class)
public class CountElement extends Entity {

    public static final String PROPERTY_COUNT = "count";
    private long count;

    private CountElement() {
    }

    public long getCount() {
        return count;
    }


    public Builder toBuilder() {
        return CountElement.Builder.newInstance()
                .count(count);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends Entity.Builder<CountElement, Builder> {

        protected Builder(CountElement countElement) {
            super(countElement);
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder(new CountElement());
        }

        public Builder count(long count) {
            entity.count = count;
            return self();
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public CountElement build() {
            super.build();
            return entity;
        }
    }
}
