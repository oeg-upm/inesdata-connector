package org.upm.inesdata.countelements.sql.index.schema;

import org.eclipse.edc.sql.translation.SqlOperatorTranslator;

import static java.lang.String.format;

/**
 * Manages Vocabularies using specific SQL queries
 */
public class BaseSqlDialectStatements implements CountElementsStatements {

    protected final SqlOperatorTranslator operatorTranslator;

    public BaseSqlDialectStatements(SqlOperatorTranslator operatorTranslator) {
        this.operatorTranslator = operatorTranslator;
    }

    @Override
    public String getCount(String entityType) {
        String tableName = null;
        switch (entityType) {
            case "asset":
                tableName = getAssetTable();
                break;
            case "policyDefinition":
                tableName = getPolicyDefinitionTable();
                break;
            case "contractDefinition":
                tableName = getContractDefinitionTable();
                break;
            case "contractAgreement":
                tableName = getContractAgreementTable();
                break;
            case "transferProcess":
                tableName = getTransferProcessTable();
                break;
        }
        return format("SELECT COUNT(*) FROM %s",
                tableName);
    }

}
