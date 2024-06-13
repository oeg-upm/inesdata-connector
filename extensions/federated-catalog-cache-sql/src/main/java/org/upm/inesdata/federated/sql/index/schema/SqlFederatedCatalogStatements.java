package org.upm.inesdata.federated.sql.index.schema;

import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.statement.SqlStatements;
import org.eclipse.edc.sql.translation.SqlQueryStatement;

@ExtensionPoint
public interface SqlFederatedCatalogStatements extends SqlStatements {

    // Catalog CRUD methods
    default String getCatalogTable() {
        return "edc_catalog";
    }

    default String getCatalogIdColumn() {
        return "id";
    }

    default String getCatalogParticipantIdColumn() {
        return "participant_id";
    }

    default String getCatalogPropertiesColumn() {
        return "properties";
    }

    default String getCatalogExpiredColumn() {
        return "expired";
    }

    String getInsertCatalogTemplate();

    String getUpdateCatalogTemplate();

    String getSelectCatalogTemplate();

    String getCountCatalogByIdClause();

    String getDeleteCatalogByParticipantIdTemplate();

    // DataService CRUD methods
    default String getDataServiceTable() {
        return "edc_data_service";
    }

    default String getDataServiceIdColumn() {
        return "id";
    }

    default String getDataServiceTermsColumn() {
        return "terms";
    }

    default String getDataServiceEndpointUrlColumn() {
        return "endpoint_url";
    }

    String getInsertDataServiceTemplate();

    String getUpdateDataServiceTemplate();

    String getSelectDataServiceTemplate();

    String getCountDataServiceByIdClause();

    String getDeleteDataServiceByIdTemplate();

    String getInsertCatalogDataServiceTemplate();

    default String getCatalogDataServiceTable() {
        return "edc_catalog_data_service";
    }


    // Dataset CRUD methods
    default String getDatasetTable() {
        return "edc_dataset";
    }

    default String getDatasetIdColumn() {
        return "id";
    }

    default String getDatasetOffersColumn() {
        return "offers";
    }

    default String getDatasetPropertiesColumn() {
        return "properties";
    }

    default String getDatasetCatalogIdColumn() {
        return "catalog_id";
    }

    String getInsertDatasetTemplate();

    String getUpdateDatasetTemplate();

    String getSelectDatasetTemplate();

    String getCountDatasetByIdClause();

    String getDeleteDatasetByIdTemplate();


    // Distribution CRUD methods
    default String getDistributionTable() {
        return "edc_distribution";
    }


    default String getDistributionFormatColumn() {
        return "format";
    }

    default String getDistributionDataServiceIdColumn() {
        return "data_service_id";
    }

    default String getDistributionDatasetIdColumn() {
        return "dataset_id";
    }
    String getInsertDistributionTemplate();

    String getUpdateDistributionTemplate();

    String getSelectDistributionTemplate();

    String getCountDistributionByIdClause();

    String getDeleteDistributionByIdTemplate();

    // Methods for creating SQL query using sub-select statements
    SqlQueryStatement createQuery(QuerySpec query);

    String getDeleteExpiredCatalogsTemplate();

    String getExpireAllCatalogsTemplate();
    String getInsertDatasetDistributionTemplate();
    String getCountVariableName();

    String getSelectDatasetsForCatalogTemplate();

    String getSelectDistributionsForDatasetTemplate();

    String getSelectDataServicesForCatalogTemplate();

    String getSelectDataServicesForIdTemplate();

    String getDeleteDistributionsForCatalogTemplate();

    String getDeleteCatalogDataServicesTemplate();

    String getDeleteOrphanDataServicesTemplate();

    String getDeleteDatasetsForCatalogTemplate();
}
