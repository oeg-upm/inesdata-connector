package org.upm.inesdata.vocabulary.validator;

import jakarta.json.JsonObject;
import org.eclipse.edc.validator.jsonobject.JsonObjectValidator;
import org.eclipse.edc.validator.jsonobject.validators.MandatoryObject;
import org.eclipse.edc.validator.jsonobject.validators.OptionalIdNotBlank;
import org.eclipse.edc.validator.spi.Validator;

import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_CATEGORY;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_JSON_SCHEMA;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_NAME;

/**
 * Validator for Vocabulary
 */
public class VocabularyValidator {

    /**
     * Defines the rules that a Vocabulary must comply with
     */
    public static Validator<JsonObject> instance() {
        return JsonObjectValidator.newValidator()
                .verifyId(OptionalIdNotBlank::new)
                .verify(PROPERTY_NAME, MandatoryObject::new)
                .verify(PROPERTY_CATEGORY, MandatoryObject::new)
                .verify(PROPERTY_JSON_SCHEMA, MandatoryJsonField::new)
                .build();
    }
}
