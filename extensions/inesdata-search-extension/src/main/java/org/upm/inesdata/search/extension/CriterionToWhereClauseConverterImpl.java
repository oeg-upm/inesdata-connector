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

package org.upm.inesdata.search.extension;

import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.sql.translation.CriterionToWhereClauseConverter;
import org.eclipse.edc.sql.translation.SqlOperatorTranslator;
import org.eclipse.edc.sql.translation.TranslationMapping;
import org.eclipse.edc.sql.translation.WhereClause;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.unmodifiableCollection;

public class CriterionToWhereClauseConverterImpl implements CriterionToWhereClauseConverter {

    private static final String GENERIC_SEARCH = "genericSearch";
    private static final String ASSET_DATA_PROPERTY = "'https://w3id.org/edc/v0.0.1/ns/assetData'";
    private static final String [] COMMON_PROPERTIES = {
            "https://w3id.org/edc/v0.0.1/ns/id",
            "https://w3id.org/edc/v0.0.1/ns/name",
            "https://w3id.org/edc/v0.0.1/ns/version",
            "https://w3id.org/edc/v0.0.1/ns/contenttype",
            "https://w3id.org/edc/v0.0.1/ns/contenttype",
            "http://purl.org/dc/terms/format",
            "http://www.w3.org/ns/dcat#keyword",
            "http://www.w3.org/ns/dcat#byteSize",
            "https://w3id.org/edc/v0.0.1/ns/shortDescription",
            "https://w3id.org/edc/v0.0.1/ns/assetType",
            "http://purl.org/dc/terms/description"
    };
    private final TranslationMapping translationMapping;
    private final SqlOperatorTranslator operatorTranslator;

    public CriterionToWhereClauseConverterImpl(TranslationMapping translationMapping, SqlOperatorTranslator operatorTranslator) {
        this.translationMapping = translationMapping;
        this.operatorTranslator = operatorTranslator;
    }

    @Override
    public WhereClause convert(Criterion criterion) {
        var operator = operatorTranslator.translate(criterion.getOperator().toLowerCase());
        if (operator == null) {
            throw new IllegalArgumentException("The operator '%s' is not supported".formatted(criterion.getOperator()));
        }

        if (!operator.rightOperandClass().isAssignableFrom(criterion.getOperandRight().getClass())) {
            throw new IllegalArgumentException("The operator '%s' requires the right-hand operand to be of type %s"
                    .formatted(criterion.getOperator(), operator.rightOperandClass().getSimpleName()));
        }

        if (criterion.getOperandLeft().toString().startsWith(ASSET_DATA_PROPERTY)) {
            return generateVocabularyWhereClause(criterion);
        } else if (GENERIC_SEARCH.equals(criterion.getOperandLeft().toString())) {
            return generateGenericPropertiesWhereClause(criterion);
        }

        var whereClause = translationMapping.getWhereClause(criterion, operator);
        if (whereClause == null) {
            return new WhereClause("0 = ?", 1);
        }

        return whereClause;
    }

    private WhereClause generateGenericPropertiesWhereClause(Criterion criterion) {
        String operator = criterion.getOperator();
        String rightValue = criterion.getOperandRight().toString();
        List<String> values = new ArrayList<>(Collections.nCopies(COMMON_PROPERTIES.length, rightValue));

        StringBuilder sqlWhereBuilder = new StringBuilder("(");
        for (int i = 0; i < COMMON_PROPERTIES.length; i++) {
            sqlWhereBuilder.append("properties ->> '")
                    .append(COMMON_PROPERTIES[i])
                    .append("' ")
                    .append(operator)
                    .append(" ?");
            if (i < COMMON_PROPERTIES.length - 1) {
                sqlWhereBuilder.append(" OR ");
            }
        }
        sqlWhereBuilder.append(")");

        return new WhereClause(sqlWhereBuilder.toString(), unmodifiableCollection(values));
    }

    private WhereClause generateVocabularyWhereClause(Criterion criterion) {
        String[] propertiesList = splitByDotOutsideQuotes(criterion.getOperandLeft().toString());
        StringBuilder sqlWhereBuilder = new StringBuilder();

        switch (propertiesList.length) {
            case 3 ->
                    generateNonObjectPropertySQL(sqlWhereBuilder, propertiesList, criterion.getOperandRight().toString(), false);
            case 4 -> {
                if  (propertiesList[3].equals("'@id'")) {
                    generateNonObjectPropertySQL(sqlWhereBuilder, propertiesList, criterion.getOperandRight().toString(), true);
                } else {
                    generateObjectPropertySQL(sqlWhereBuilder, propertiesList, criterion.getOperandRight().toString());
                }

            }

            default -> throw new InvalidRequestException("Invalid vocabulary argument in the operandLeft: %s"
                    .formatted(criterion.getOperandLeft().toString()));
        }

        return new WhereClause(sqlWhereBuilder.toString(), unmodifiableCollection(new ArrayList<>()));
    }

    private void generateObjectPropertySQL(StringBuilder sqlWhereBuilder, String[] propertiesList, String operandRight) {
        sqlWhereBuilder.append("EXISTS (SELECT 1 FROM jsonb_array_elements((properties::jsonb -> ")
                .append(propertiesList[0])
                .append(" -> ")
                .append(propertiesList[1])
                .append(")::jsonb) AS vocab WHERE vocab -> ")
                .append(propertiesList[2])
                .append(" @> '[{")
                .append(propertiesList[3].replaceAll("'", "\""))
                .append(": [{\"@value\": \"")
                .append(operandRight)
                .append("\"}]}]')");
    }

    private void generateNonObjectPropertySQL(StringBuilder sqlWhereBuilder, String[] propertiesList, String operandRight, boolean isIdProperty) {
        sqlWhereBuilder.append("(properties::jsonb -> ")
                .append(propertiesList[0])
                .append(" -> ")
                .append(propertiesList[1])
                .append(")::jsonb @> '[{")
                .append(propertiesList[2].replaceAll("'", "\""))
                .append(isIdProperty ? ": [{\"@id\": \"" : ": [{\"@value\": \"")
                .append(operandRight)
                .append("\"}]}]'::jsonb");
    }

    private String[] splitByDotOutsideQuotes(String input) {
        List<String> parts = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\.(?=(?:[^']*'[^']*')*[^']*$)");

        Matcher matcher = pattern.matcher(input);
        int start = 0;

        while (matcher.find()) {
            String part = input.substring(start, matcher.start()).trim();
            parts.add(part);
            start = matcher.end();
        }

        if (start < input.length()) {
            String lastPart = input.substring(start).trim();
            parts.add(lastPart);
        }

        return parts.toArray(new String[0]);
    }
}
