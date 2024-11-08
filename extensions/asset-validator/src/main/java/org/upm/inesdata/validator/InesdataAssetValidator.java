/*
 *  Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.upm.inesdata.validator;

import jakarta.json.JsonObject;
import org.eclipse.edc.validator.jsonobject.JsonObjectValidator;
import org.eclipse.edc.validator.jsonobject.validators.MandatoryObject;
import org.eclipse.edc.validator.jsonobject.validators.MandatoryValue;
import org.eclipse.edc.validator.jsonobject.validators.OptionalIdNotBlank;
import org.eclipse.edc.validator.spi.ValidationResult;
import org.eclipse.edc.validator.spi.Validator;
import org.eclipse.edc.validator.spi.Violation;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset.EDC_ASSET_DATA_ADDRESS;
import static org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset.EDC_ASSET_PROPERTIES;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.VALUE;
import static org.eclipse.edc.jsonld.spi.Namespaces.DCAT_SCHEMA;
import static org.eclipse.edc.jsonld.spi.Namespaces.DCT_SCHEMA;
import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;
import static org.eclipse.edc.spi.types.domain.DataAddress.EDC_DATA_ADDRESS_TYPE_PROPERTY;
import static org.eclipse.edc.validator.spi.Violation.violation;

/**
 * Custom asset validation
 */
public class InesdataAssetValidator {

    public static final String PROPERTY_NAME = EDC_NAMESPACE + "name";
    public static final String PROPERTY_VERSION = EDC_NAMESPACE + "version";
    public static final String PROPERTY_SHORT_DESCRIPTION = EDC_NAMESPACE + "shortDescription";
    public static final String PROPERTY_DESCRIPTION = DCT_SCHEMA + "description";
    public static final String PROPERTY_ASSET_TYPE = EDC_NAMESPACE + "assetType";
    public static final String PROPERTY_KEYWORD = DCAT_SCHEMA + "keyword";
    public static final String PROPERTY_AMAZONS3_REGION = EDC_NAMESPACE + "region";
    public static final String PROPERTY_HTTP_DATA_BASE_URL = EDC_NAMESPACE + "baseUrl";

    public static Validator<JsonObject> instance() {
        Validator<JsonObject> originalValidator = JsonObjectValidator.newValidator()
                .verifyId(OptionalIdNotBlank::new)
                .verify(EDC_ASSET_PROPERTIES, MandatoryObject::new)
                .verify(EDC_ASSET_DATA_ADDRESS, MandatoryObject::new)
                .verifyObject(EDC_ASSET_PROPERTIES, propertiesBuilder ->
                        propertiesBuilder
                                .verify(PROPERTY_NAME, MandatoryValue::new)
                                .verify(PROPERTY_VERSION, MandatoryObject::new)
                                .verify(PROPERTY_SHORT_DESCRIPTION, MandatoryValue::new)
                                .verify(PROPERTY_DESCRIPTION, MandatoryValue::new)
                                .verify(PROPERTY_ASSET_TYPE, MandatoryValue::new)
                                .verify(PROPERTY_KEYWORD, MandatoryValue::new)
                )
                .verifyObject(EDC_ASSET_DATA_ADDRESS, dataAddressBuilder ->
                        dataAddressBuilder
                                .verify(EDC_DATA_ADDRESS_TYPE_PROPERTY, path -> new TypeBasedValidator())
                ).build();

        return jsonObject -> {
            ValidationResult result = originalValidator.validate(jsonObject);

            if (result.failed()) {
                return ValidationResult.failure(
                        result.getFailure().getViolations().stream()
                                .map(InesdataAssetValidator::updateViolationMessage)
                                .collect(Collectors.toList())
                );
            }

            return result;
        };
    }

    private static Violation updateViolationMessage(Violation violation) {
        String path = violation.path();

        if (path.startsWith(EDC_ASSET_PROPERTIES)) {
            String property = path.substring(EDC_ASSET_PROPERTIES.length() + 1);
            String updatedMessage = String.format("mandatory field '%s' is missing or it is blank", property);
            return Violation.violation(updatedMessage, property);
        }
        return violation;
    }

    private static class TypeBasedValidator implements Validator<JsonObject> {
        @Override
        public ValidationResult validate(JsonObject dataAddress) {
            String type = extractValueFromJsonArray(dataAddress, EDC_DATA_ADDRESS_TYPE_PROPERTY);

            if (type == null) {
                return ValidationResult.failure(violation(
                        "The 'https://w3id.org/edc/v0.0.1/ns/type' field is missing or invalid in dataAddress",
                        EDC_DATA_ADDRESS_TYPE_PROPERTY
                ));
            }

            return switch (type) {
                case "AmazonS3" -> validateAmazonS3(dataAddress);
                case "HttpData" -> validateHttpData(dataAddress);
                case "InesDataStore" -> ValidationResult.success();
                default -> ValidationResult.failure(violation(
                        "The value for 'https://w3id.org/edc/v0.0.1/ns/type' field is not valid",
                        EDC_DATA_ADDRESS_TYPE_PROPERTY
                ));
            };
        }

        private ValidationResult validateAmazonS3(JsonObject dataAddress) {
            var violations = new ArrayList<Violation>();

            if (extractValueFromJsonArray(dataAddress, PROPERTY_AMAZONS3_REGION) == null) {
                violations.add(violation(
                        "Field 'https://w3id.org/edc/v0.0.1/ns/region' is required for AmazonS3 DataAddress type",
                        PROPERTY_AMAZONS3_REGION
                ));
            }

            return violations.isEmpty() ? ValidationResult.success() : ValidationResult.failure(violations);
        }

        private ValidationResult validateHttpData(JsonObject dataAddress) {
            var violations = new ArrayList<Violation>();

            if (extractValueFromJsonArray(dataAddress, PROPERTY_NAME) == null) {
                violations.add(violation(
                        "Field 'https://w3id.org/edc/v0.0.1/ns/name' is required for HttpData DataAddress type",
                        PROPERTY_NAME
                ));
            }

            if (extractValueFromJsonArray(dataAddress, PROPERTY_HTTP_DATA_BASE_URL) == null) {
                violations.add(violation(
                        "Field 'https://w3id.org/edc/v0.0.1/ns/baseUrl' is required for HttpData DataAddress type",
                        PROPERTY_HTTP_DATA_BASE_URL
                ));
            }

            return violations.isEmpty() ? ValidationResult.success() : ValidationResult.failure(violations);
        }

        private String extractValueFromJsonArray(JsonObject jsonObject, String key) {
            return Optional.ofNullable(jsonObject.getJsonArray(key))
                    .filter(array -> !array.isEmpty())
                    .map(array -> array.getJsonObject(0))
                    .map(obj -> obj.getString(VALUE))
                    .filter(value -> !value.isBlank())
                    .orElse(null);
        }
    }
}

