package org.upm.inesdata.countelements.sql.index.schema;

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.translation.SqlOperatorTranslator;
import org.upm.inesdata.countelements.sql.index.schema.postgres.SqlDatasetMapping;
import org.upm.inesdata.search.extension.InesdataSqlQueryStatement;

import static java.lang.String.format;

/**
 * Manages Vocabularies using specific SQL queries
 */
public class BaseSqlDialectStatements implements CountElementsStatements {

    protected final SqlOperatorTranslator operatorTranslator;

    public BaseSqlDialectStatements(SqlOperatorTranslator operatorTranslator) {
        this.operatorTranslator = operatorTranslator;
    }

    /**
     * {@inheritDoc}
     *
     * @see CountElementsStatements#getCount(String)
     */
    @Override
    public String getCount(String entityType) {
        String tableName = switch (entityType) {
            case "asset" -> getAssetTable();
            case "policyDefinition" -> getPolicyDefinitionTable();
            case "contractDefinition" -> getContractDefinitionTable();
            case "contractAgreement" -> getContractAgreementTable();
            case "transferProcess" -> getTransferProcessTable();
            case "federatedCatalog" -> getDatasetTable();
            default -> null;
        };
        return format("SELECT COUNT(*) FROM %s",
                tableName);
    }

    /**
     * {@inheritDoc}
     *
     * @see CountElementsStatements#createDatasetQuery(QuerySpec)
     */
    @Override
    public InesdataSqlQueryStatement createCountDatasetQuery(String entityType, QuerySpec querySpec) {
        return new InesdataSqlQueryStatement(getCount(entityType), querySpec, new SqlDatasetMapping(this), operatorTranslator);
    }
}
