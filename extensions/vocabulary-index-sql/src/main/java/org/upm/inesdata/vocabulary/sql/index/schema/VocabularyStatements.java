package org.upm.inesdata.vocabulary.sql.index.schema;

import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.statement.SqlStatements;
import org.eclipse.edc.sql.translation.SqlQueryStatement;

/**
 * Defines queries used by the SqlVocabularyIndexServiceExtension.
 */
@ExtensionPoint
public interface VocabularyStatements extends SqlStatements {

    /**
     * The vocabulary table name.
     */
    default String getVocabularyTable() {
        return "edc_vocabulary";
    }

    /**
     * The vocabulary table ID column.
     */
    default String getVocabularyIdColumn() {
        return "id";
    }

    default String getNameColumn() {
        return "name";
    }

    default String getConnectorIdColumn() {
        return "connector_id";
    }

    default String getJsonSchemaColumn() {
        return "json_schema";
    }

    default String getCategoryColumn() {
        return "category";
    }

    default String getCreatedAtColumn() {
        return "created_at";
    }


    /**
     * INSERT clause for vocabularys.
     */
    String getInsertVocabularyTemplate();

    /**
     * UPDATE clause for vocabularys.
     */
    String getUpdateVocabularyTemplate();

    /**
     * SELECT COUNT clause for vocabularys.
     */
    String getCountVocabularyByIdAndConnectorIdClause();

    /**
     * SELECT clause for all vocabularys.
     */
    String getSelectVocabularyTemplate();

    /**
     * DELETE clause for vocabularys.
     */
    String getDeleteVocabularyByIdTemplate();

    /**
     * DELETE clause for vocabularies by connectorId.
     */
    String getDeleteVocabulariesByConnectorIdTemplate();

    /**
     * The COUNT variable used in SELECT COUNT queries.
     */
    String getCountVariableName();

    /**
     * Generates a SQL query using sub-select statements out of the query spec.
     *
     * @param query querySpec to be executed
     * @return A {@link SqlQueryStatement} that contains the SQL and statement parameters
     */
    SqlQueryStatement createQuery(QuerySpec query);

    /**
     * DELETE clause for vocabularies by ID and connector ID.
     */
    String getDeleteVocabularyByIdAndConnectorIdTemplate();

}
