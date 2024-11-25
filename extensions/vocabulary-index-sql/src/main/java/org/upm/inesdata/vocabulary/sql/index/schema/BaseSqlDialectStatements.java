package org.upm.inesdata.vocabulary.sql.index.schema;

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.translation.SqlOperatorTranslator;
import org.eclipse.edc.sql.translation.SqlQueryStatement;
import org.upm.inesdata.vocabulary.sql.index.schema.postgres.VocabularyMapping;

import static java.lang.String.format;

/**
 * Manages Vocabularies using specific SQL queries
 */
public class BaseSqlDialectStatements implements VocabularyStatements {

    protected final SqlOperatorTranslator operatorTranslator;

    public BaseSqlDialectStatements(SqlOperatorTranslator operatorTranslator) {
        this.operatorTranslator = operatorTranslator;
    }

    @Override
    public String getInsertVocabularyTemplate() {
        return executeStatement()
                .column(getVocabularyIdColumn())
                .column(getCreatedAtColumn())
                .column(getNameColumn())
                .column(getConnectorIdColumn())
                .column(getCategoryColumn())
                .jsonColumn(getJsonSchemaColumn())
                .insertInto(getVocabularyTable());
    }

    @Override
    public String getUpdateVocabularyTemplate() {
        return executeStatement()
                .column(getNameColumn())
                .jsonColumn(getJsonSchemaColumn())
                .column(getCategoryColumn())
                .update(getVocabularyTable(), getVocabularyIdColumn());
    }

    @Override
    public String getCountVocabularyByIdAndConnectorIdClause() {
        return format("SELECT COUNT(*) AS %s FROM %s WHERE %s = ? AND %s = ?",
                getCountVariableName(),
                getVocabularyTable(),
                getVocabularyIdColumn(),
                getConnectorIdColumn());
    }

    @Override
    public String getSelectVocabularyTemplate() {
        return format("SELECT * FROM %s AS a", getVocabularyTable());
    }

    @Override
    public String getDeleteVocabularyByIdTemplate() {
        return executeStatement()
                .delete(getVocabularyTable(), getVocabularyIdColumn());
    }

    @Override
    public String getDeleteVocabulariesByConnectorIdTemplate() {
        return executeStatement()
                .delete(getVocabularyTable(), getConnectorIdColumn());
    }

    @Override
    public String getCountVariableName() {
        return "COUNT";
    }

    @Override
    public SqlQueryStatement createQuery(QuerySpec querySpec) {
        return new SqlQueryStatement(getSelectVocabularyTemplate(), querySpec, new VocabularyMapping(this), operatorTranslator);
    }

    @Override
    public String getDeleteVocabularyByIdAndConnectorIdTemplate() {
        return format("DELETE FROM %s WHERE %s = ? AND %s = ?",
                getVocabularyTable(),
                getVocabularyIdColumn(),
                getConnectorIdColumn());
    }
}
