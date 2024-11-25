package org.upm.inesdata.vocabulary.sql.index.schema.postgres;

import org.eclipse.edc.sql.translation.JsonFieldTranslator;
import org.eclipse.edc.sql.translation.TranslationMapping;
import org.upm.inesdata.vocabulary.sql.index.schema.VocabularyStatements;

/**
 * Maps fields of a {@link org.eclipse.edc.spi.types.domain.vocabulary.Vocabulary} onto the
 * corresponding SQL schema (= column names) enabling access through Postgres JSON operators where applicable
 */
public class VocabularyMapping extends TranslationMapping {

    public VocabularyMapping(VocabularyStatements statements) {
        add("id", statements.getVocabularyIdColumn());
        add("createdAt", statements.getCreatedAtColumn());
        add("name", statements.getNameColumn());
        add("connectorId", statements.getConnectorIdColumn());
        add("category", statements.getCategoryColumn());
        add("json_schema", new JsonFieldTranslator(statements.getJsonSchemaColumn()));
    }

}
