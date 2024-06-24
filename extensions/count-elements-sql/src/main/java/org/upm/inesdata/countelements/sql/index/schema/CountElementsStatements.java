package org.upm.inesdata.countelements.sql.index.schema;

import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;
import org.eclipse.edc.sql.statement.SqlStatements;

/**
 * Defines queries used by the SqlCountElementsIndexServiceExtension.
 */
@ExtensionPoint
public interface CountElementsStatements extends SqlStatements {

    /**
     * The asset table name.
     */
    default String getAssetTable() {
        return "edc_asset";
    }

    /**
     * The policy definition table name.
     */
    default String getPolicyDefinitionTable() {
        return "edc_policydefinitions";
    }

    /**
     * The contract agreement table name.
     */
    default String getContractAgreementTable() {
        return "edc_contract_agreement";
    }

    /**
     * The contract definition table name.
     */
    default String getContractDefinitionTable() {
        return "edc_contract_definitions";
    }

    /**
     * The transfer process table name.
     */
    default String getTransferProcessTable() {
        return "edc_transfer_process";
    }

    /**
     * The dataset table name.
     */
    default String getDatasetTable() {
        return "edc_dataset";
    }

    /**
     * SELECT COUNT clause.
     */
    String getCount(String entityType);

}
