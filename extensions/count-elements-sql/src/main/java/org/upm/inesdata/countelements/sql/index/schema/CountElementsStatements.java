package org.upm.inesdata.countelements.sql.index.schema;

import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.statement.SqlStatements;
import org.upm.inesdata.search.extension.InesdataSqlQueryStatement;

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
     * Retrieves the name of the column storing dataset IDs.
     *
     * @return the name of the dataset ID column.
     */
    default String getDatasetIdColumn() {
        return "id";
    }

    /**
     * Retrieves the name of the column storing offers associated with datasets.
     *
     * @return the name of the offers column.
     */
    default String getDatasetOffersColumn() {
        return "offers";
    }

    /**
     * Retrieves the name of the column storing properties of datasets as JSON.
     *
     * @return the name of the properties column.
     */
    default String getDatasetPropertiesColumn() {
        return "properties";
    }

    /**
     * Retrieves the name of the column storing the catalog ID associated with datasets.
     *
     * @return the name of the catalog ID column.
     */
    default String getDatasetCatalogIdColumn() {
        return "catalog_id";
    }

    /**
     * Creates an SQL query statement specifically for datasets based on the provided query specification.
     *
     * @param querySpec the query specification defining filters, sorting, and pagination for datasets.
     * @param entityType the entity type (federatedCatalog)
     * @return an SQL query statement for datasets.
     */
    InesdataSqlQueryStatement createCountDatasetQuery(String entityType, QuerySpec querySpec);

    /**
     * SELECT COUNT clause.
     */
    String getCount(String entityType);

}
