package org.upm.inesdata.federated.sql.index.schema;

import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.statement.SqlStatements;
import org.eclipse.edc.sql.translation.SqlQueryStatement;
import org.upm.inesdata.search.extension.InesdataSqlQueryStatement;

/**
 * SQL statements interface for managing federated catalog data. Extends {@link SqlStatements} and provides methods for
 * retrieving SQL queries and templates related to CRUD operations on catalogs, data services, datasets, distributions,
 * and other related entities.
 *
 * This interface includes default methods for retrieving table names and column names specific to each entity, as well
 * as methods for generating SQL templates and statements for various operations.
 */
@ExtensionPoint
public interface SqlFederatedCatalogStatements extends SqlStatements {

  /**
   * Retrieves the name of the catalog table.
   *
   * @return the name of the catalog table.
   */
  default String getCatalogTable() {
    return "edc_catalog";
  }

  /**
   * Retrieves the name of the column storing catalog IDs.
   *
   * @return the name of the catalog ID column.
   */
  default String getCatalogIdColumn() {
    return "id";
  }

  /**
   * Retrieves the name of the column storing participant IDs associated with catalogs.
   *
   * @return the name of the participant ID column.
   */
  default String getCatalogParticipantIdColumn() {
    return "participant_id";
  }

  /**
   * Retrieves the name of the column storing properties of catalogs as JSON.
   *
   * @return the name of the properties column.
   */
  default String getCatalogPropertiesColumn() {
    return "properties";
  }

  /**
   * Retrieves the name of the column indicating if a catalog has expired.
   *
   * @return the name of the expired column.
   */
  default String getCatalogExpiredColumn() {
    return "expired";
  }

  /**
   * Retrieves the SQL template for inserting a new catalog.
   *
   * @return the SQL template for inserting a catalog.
   */
  String getInsertCatalogTemplate();

  /**
   * Retrieves the SQL template for selecting catalogs.
   *
   * @return the SQL template for selecting catalogs.
   */
  String getSelectCatalogTemplate();

  /**
   * Retrieves the SQL template for deleting catalogs by participant ID.
   *
   * @return the SQL template for deleting catalogs by participant ID.
   */
  String getDeleteCatalogByParticipantIdTemplate();

  /**
   * Retrieves the name of the data service table.
   *
   * @return the name of the data service table.
   */
  default String getDataServiceTable() {
    return "edc_data_service";
  }

  /**
   * Retrieves the name of the column storing data service IDs.
   *
   * @return the name of the data service ID column.
   */
  default String getDataServiceIdColumn() {
    return "id";
  }

  /**
   * Retrieves the name of the column storing terms associated with data services.
   *
   * @return the name of the terms column.
   */
  default String getDataServiceTermsColumn() {
    return "terms";
  }

  /**
   * Retrieves the name of the column storing endpoint URLs of data services.
   *
   * @return the name of the endpoint URL column.
   */
  default String getDataServiceEndpointUrlColumn() {
    return "endpoint_url";
  }

  /**
   * Retrieves the SQL template for inserting a new data service.
   *
   * @return the SQL template for inserting a data service.
   */
  String getInsertDataServiceTemplate();

  /**
   * Retrieves the SQL template for inserting a mapping between a catalog and a data service.
   *
   * @return the SQL template for inserting a catalog-data service mapping.
   */
  String getInsertCatalogDataServiceTemplate();

  /**
   * Retrieves the name of the table storing mappings between catalogs and data services.
   *
   * @return the name of the catalog-data service mapping table.
   */
  default String getCatalogDataServiceTable() {
    return "edc_catalog_data_service";
  }

  /**
   * Retrieves the name of the dataset table.
   *
   * @return the name of the dataset table.
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
   * Retrieves the SQL template for inserting a new dataset.
   *
   * @return the SQL template for inserting a dataset.
   */
  String getInsertDatasetTemplate();

  /**
   * Retrieves the name of the distribution table.
   *
   * @return the name of the distribution table.
   */
  default String getDistributionTable() {
    return "edc_distribution";
  }

  /**
   * Retrieves the name of the column storing distribution formats.
   *
   * @return the name of the format column.
   */
  default String getDistributionFormatColumn() {
    return "format";
  }

  /**
   * Retrieves the name of the column storing data service IDs associated with distributions.
   *
   * @return the name of the data service ID column.
   */
  default String getDistributionDataServiceIdColumn() {
    return "data_service_id";
  }

  /**
   * Retrieves the name of the column storing dataset IDs associated with distributions.
   *
   * @return the name of the dataset ID column.
   */
  default String getDistributionDatasetIdColumn() {
    return "dataset_id";
  }

  /**
   * Retrieves the SQL template for inserting a new distribution.
   *
   * @return the SQL template for inserting a distribution.
   */
  String getInsertDistributionTemplate();

  /**
   * Creates an SQL query statement based on the provided query specification.
   *
   * @param query the query specification defining filters, sorting, and pagination.
   * @return an SQL query statement.
   */
  SqlQueryStatement createQuery(QuerySpec query);

  /**
   * Creates an SQL query statement specifically for datasets based on the provided query specification.
   *
   * @param querySpec the query specification defining filters, sorting, and pagination for datasets.
   * @return an SQL query statement for datasets.
   */
  InesdataSqlQueryStatement createDatasetQuery(QuerySpec querySpec);

  /**
   * Retrieves the SQL template for deleting expired catalogs.
   *
   * @return the SQL template for deleting expired catalogs.
   */
  String getDeleteExpiredCatalogsTemplate();

  /**
   * Retrieves the SQL template for expiring all catalogs.
   *
   * @return the SQL template for expiring all catalogs.
   */
  String getExpireAllCatalogsTemplate();

  /**
   * Retrieves the SQL template for selecting datasets associated with a catalog.
   *
   * @return the SQL template for selecting datasets for a catalog.
   */
  String getSelectDatasetsForCatalogTemplate();

  /**
   * Retrieves the SQL template for selecting distributions associated with a dataset.
   *
   * @return the SQL template for selecting distributions for a dataset.
   */
  String getSelectDistributionsForDatasetTemplate();

  /**
   * Retrieves the SQL template for selecting data services associated with a catalog.
   *
   * @return the SQL template for selecting data services for a catalog.
   */
  String getSelectDataServicesForCatalogTemplate();

  /**
   * Retrieves the SQL template for selecting data services by ID.
   *
   * @return the SQL template for selecting data services by ID.
   */
  String getSelectDataServicesForIdTemplate();

  /**
   * Retrieves the SQL template for deleting distributions associated with a catalog.
   *
   * @return the SQL template for deleting distributions for a catalog.
   */
  String getDeleteDistributionsForCatalogTemplate();

  /**
   * Retrieves the SQL template for deleting mappings between catalogs and data services.
   *
   * @return the SQL template for deleting catalog-data service mappings.
   */
  String getDeleteCatalogDataServicesTemplate();

  /**
   * Retrieves the SQL template for deleting orphaned data services.
   *
   * @return the SQL template for deleting orphaned data services.
   */
  String getDeleteOrphanDataServicesTemplate();

  /**
   * Retrieves the SQL template for deleting datasets associated with a catalog.
   *
   * @return the SQL template for deleting datasets for a catalog.
   */
  String getDeleteDatasetsForCatalogTemplate();

  /**
   * Retrieves the SQL template for selecting a catalog by participant ID.
   *
   * @return the SQL template for selecting a catalog by participant ID.
   */
  String getSelectCatalogForParticipantIdTemplate();

  /**
   * Retrieves the SQL template for selecting a dataset.
   *
   * @return the SQL template for selecting a dataset.
   */
  String getSelectDatasetTemplate();

  /**
   * Retrieves the SQL template for expired catalogs.
   *
   * @return the SQL template for selecting expired catalogs.
   */
  String getSelectExpiredCatalogsTemplate();
}
