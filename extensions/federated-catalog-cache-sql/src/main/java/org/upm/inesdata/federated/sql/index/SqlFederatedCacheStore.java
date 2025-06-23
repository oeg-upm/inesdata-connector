package org.upm.inesdata.federated.sql.index;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.JsonObject;
import org.eclipse.edc.connector.controlplane.catalog.spi.Catalog;
import org.eclipse.edc.connector.controlplane.catalog.spi.DataService;
import org.eclipse.edc.connector.controlplane.catalog.spi.Dataset;
import org.eclipse.edc.connector.controlplane.catalog.spi.Distribution;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.constants.CoreConstants;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.StoreResult;
import org.eclipse.edc.sql.QueryExecutor;
import org.eclipse.edc.sql.store.AbstractSqlStore;
import org.eclipse.edc.sql.translation.SqlQueryStatement;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.upm.inesdata.federated.sql.index.schema.SqlFederatedCatalogStatements;
import org.upm.inesdata.spi.federated.index.PaginatedFederatedCacheStoreIndex;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of the {@link PaginatedFederatedCacheStoreIndex} that uses SQL for storing and retrieving federated
 * catalog data. This class extends {@link AbstractSqlStore} and provides methods for saving, querying, and managing
 * catalogs in a federated cache.
 */
@SuppressWarnings("unchecked")
public class SqlFederatedCacheStore extends AbstractSqlStore implements PaginatedFederatedCacheStoreIndex {

  public static final String INTERNAL_CATALOG_ID = "internal_catalog_id";
  private final SqlFederatedCatalogStatements databaseStatements;

  /**
   * Constructs a SqlFederatedCacheStore with the specified dependencies.
   *
   * @param dataSourceRegistry the registry for data sources.
   * @param dataSourceName     the name of the data source.
   * @param transactionContext the context for handling transactions.
   * @param objectMapper       the object mapper for JSON processing.
   * @param databaseStatements the SQL statements specific to the federated catalog.
   * @param queryExecutor      the executor for running SQL queries.
   */
  public SqlFederatedCacheStore(DataSourceRegistry dataSourceRegistry, String dataSourceName,
      TransactionContext transactionContext, ObjectMapper objectMapper,
      SqlFederatedCatalogStatements databaseStatements, QueryExecutor queryExecutor) {
    super(dataSourceRegistry, dataSourceName, transactionContext, objectMapper, queryExecutor);
    this.databaseStatements = Objects.requireNonNull(databaseStatements);
  }

  /**
   * Saves the provided catalog into the federated cache database.
   *
   * @param catalog the catalog to be saved.
   * @throws NullPointerException if the catalog is null.
   */
  @Override
  public void save(Catalog catalog) {
    Objects.requireNonNull(catalog);
    transactionContext.execute(() -> {
      try (var connection = getConnection()) {
        deleteRelatedCatalogData(connection, catalog);
        return StoreResult.success();
      } catch (Exception e) {
        throw new EdcPersistenceException(e);
      }
    });
    transactionContext.execute(() -> {
      try (var connection = getConnection()) {
        insertCatalog(catalog, connection);
        insertDataServices(catalog, connection);
        insertDatasets(catalog, connection);
        return StoreResult.success();
      } catch (Exception e) {
        throw new EdcPersistenceException(e);
      }
    });
  }

  /**
   * Queries the federated cache based on the provided criteria.
   *
   * @param query the list of criteria to filter the query.
   * @return a collection of catalogs that match the query criteria.
   * @throws EdcPersistenceException if a SQL error occurs during the query.
   */
  @Override
  public Collection<Catalog> query(QuerySpec query) {
    return transactionContext.execute(() -> {
      try (var connection = getConnection()) {
        SqlQueryStatement queryStatement = databaseStatements.createQuery(query);
        return queryExecutor.query(connection, true, this::mapResultSetToCatalog, queryStatement.getQueryAsString(),
            queryStatement.getParameters()).collect(Collectors.toList());
      } catch (SQLException e) {
        throw new EdcPersistenceException(e);
      }
    });
  }

  /**
   * Deletes expired catalogs from the federated cache database.
   *
   * @throws EdcPersistenceException if a SQL error occurs during the deletion.
   */
  @Override
  public void deleteExpired() {
    transactionContext.execute(() -> {
      try (var connection = getConnection()) {
        List<Catalog> expiredCatalogs = getExpiredCatalogs();
        for (Catalog catalog: expiredCatalogs) {
          this.deleteRelatedCatalogData(connection, catalog);
        }
        return null;
      } catch (SQLException e) {
        throw new EdcPersistenceException(e);
      }
    });
  }

  /**
   * Expires all catalogs in the federated cache database.
   *
   * @throws EdcPersistenceException if a SQL error occurs during the operation.
   */
  @Override
  public void expireAll() {
    transactionContext.execute(() -> {
      try (var connection = getConnection()) {
        queryExecutor.execute(connection, databaseStatements.getExpireAllCatalogsTemplate(), true, false);
        return null;
      } catch (SQLException e) {
        throw new EdcPersistenceException(e);
      }
    });
  }

  /**
   * (non-javadoc)
   *
   * @see PaginatedFederatedCacheStoreIndex#queryPagination(QuerySpec)
   */
  @Override
  public Collection<Catalog> queryPagination(QuerySpec querySpec) {
    return transactionContext.execute(() -> {
      try (Connection connection = getConnection()) {
        return getPaginatedCatalogs(querySpec, connection);
      } catch (SQLException e) {
        throw new EdcPersistenceException(e);
      }
    });
  }

  private List<Catalog> getExpiredCatalogs() {
    try (var connection = getConnection()) {
      String selectCatalog = databaseStatements.getSelectExpiredCatalogsTemplate();
      return queryExecutor.query(connection, false, this::mapResultSetToCatalog, selectCatalog)
              .collect(Collectors.toList());
    } catch (SQLException e) {
      throw new EdcPersistenceException(e);
    }
  }

  private void deleteRelatedCatalogData(Connection connection, Catalog catalog) {
    Catalog catalogByParticipantId = getCatalogByParticipantId(catalog.getParticipantId());

    if (catalogByParticipantId != null && catalogByParticipantId.getId() != null) {
      String deleteDistributionsSql = databaseStatements.getDeleteDistributionsForCatalogTemplate();
      queryExecutor.execute(connection, deleteDistributionsSql, catalogByParticipantId.getId());

      String deleteCatalogDataServicesSql = databaseStatements.getDeleteCatalogDataServicesTemplate();
      queryExecutor.execute(connection, deleteCatalogDataServicesSql, catalogByParticipantId.getId());

      String deleteOrphanDataServicesSql = databaseStatements.getDeleteOrphanDataServicesTemplate();
      queryExecutor.execute(connection, deleteOrphanDataServicesSql);

      String deleteDatasetsSql = databaseStatements.getDeleteDatasetsForCatalogTemplate();
      queryExecutor.execute(connection, deleteDatasetsSql, catalogByParticipantId.getId());

      String deleteCatalogSql = databaseStatements.getDeleteCatalogByParticipantIdTemplate();
      queryExecutor.execute(connection, deleteCatalogSql, catalog.getParticipantId());
    }
  }

  private boolean dataServiceExists(String dataServiceId) throws SQLException {
    return getDataServiceById(dataServiceId) != null;
  }

  private Catalog mapResultSetToCatalog(ResultSet resultSet) throws SQLException {
    String id = resultSet.getString("id");
    String participantId = resultSet.getString("participant_id");
    Map<String, Object> properties = fromJson(resultSet.getString("properties"), Map.class);
    List<Dataset> datasets = getDatasetsForCatalog(id);
    List<DataService> dataServices = getDataServicesForCatalog(id);

    return Catalog.Builder.newInstance().id(id).participantId(participantId).datasets(datasets)
        .dataServices(dataServices).properties(properties).build();
  }

  private List<Dataset> getDatasetsForCatalog(String catalogId) {
    try (var connection = getConnection()) {
      String sql = databaseStatements.getSelectDatasetsForCatalogTemplate();
      return queryExecutor.query(connection, false, rs -> mapResultSetToDataset(rs, false), sql, catalogId)
          .collect(Collectors.toList());
    } catch (SQLException e) {
      throw new EdcPersistenceException(e);
    }
  }

  private Dataset mapResultSetToDataset(ResultSet resultSet, boolean withCatalogId) throws SQLException {
    String id = resultSet.getString("id");
    Map<String, Object> properties = fromJson(resultSet.getString("properties"), new TypeReference<Map<String, Object>>() {});
    Map<String, Policy> offers = fromJson(resultSet.getString("offers"), new TypeReference<Map<String, Policy>>() {
    });

    List<Distribution> distributions = getDistributionsForDataset(id);

    if (withCatalogId) {
      properties.put(INTERNAL_CATALOG_ID, resultSet.getString("catalog_id"));
    }
    Dataset.Builder datasetBuilder = Dataset.Builder.newInstance().id(id).properties(properties)
            .distributions(distributions);

    if (offers != null) {
      offers.forEach((key, value) -> datasetBuilder.offer(key, value));
    }

    return datasetBuilder.build();
  }

  private List<Distribution> getDistributionsForDataset(String datasetId) {
    try (var connection = getConnection()) {
      String sql = databaseStatements.getSelectDistributionsForDatasetTemplate();
      return queryExecutor.query(connection, false, this::mapResultSetToDistribution, sql, datasetId)
          .collect(Collectors.toList());
    } catch (SQLException e) {
      throw new EdcPersistenceException(e);
    }
  }

  private Distribution mapResultSetToDistribution(ResultSet resultSet) throws SQLException {
    String format = resultSet.getString("format");
    String dataServiceId = resultSet.getString("data_service_id");

    DataService dataService = getDataServiceById(dataServiceId);

    return Distribution.Builder.newInstance().format(format).dataService(dataService).build();
  }

  private DataService getDataServiceById(String dataServiceId) {
    try (var connection = getConnection()) {
      String sql = databaseStatements.getSelectDataServicesForIdTemplate();
      return queryExecutor.query(connection, false, this::mapResultSetToDataService, sql, dataServiceId).findFirst()
          .orElse(null);
    } catch (SQLException e) {
      throw new EdcPersistenceException(e);
    }
  }

  private Catalog getCatalogByParticipantId(String participantId) {
    try (var connection = getConnection()) {
      String selectCatalog = databaseStatements.getSelectCatalogForParticipantIdTemplate();
      return queryExecutor.query(connection, false, this::mapResultSetToCatalogSimple, selectCatalog, participantId)
          .findFirst().orElse(null);
    } catch (SQLException e) {
      throw new EdcPersistenceException(e);
    }
  }

  private Catalog mapResultSetToCatalogSimple(ResultSet resultSet) throws SQLException {
    String id = resultSet.getString("id");
    return Catalog.Builder.newInstance().id(id).build();
  }

  private List<DataService> getDataServicesForCatalog(String catalogId) {
    try (var connection = getConnection()) {
      String sql = databaseStatements.getSelectDataServicesForCatalogTemplate();
      return queryExecutor.query(connection, false, this::mapResultSetToDataService, sql, catalogId)
          .collect(Collectors.toList());
    } catch (SQLException e) {
      throw new EdcPersistenceException(e);
    }
  }

  private DataService mapResultSetToDataService(ResultSet resultSet) throws SQLException {
    String id = resultSet.getString("id");
    String terms = resultSet.getString("terms");
    String endpointUrl = resultSet.getString("endpoint_url");

    return DataService.Builder.newInstance().id(id).endpointDescription(terms).endpointUrl(endpointUrl).build();
  }

  private Catalog.Builder mapResultSetToCatalogNoDependencies(ResultSet resultSet) throws SQLException {
    String id = resultSet.getString("id");
    String participantId = resultSet.getString("participant_id");
    Map<String, Object> properties = fromJson(resultSet.getString("properties"), Map.class);

    return Catalog.Builder.newInstance().id(id).participantId(participantId).properties(properties);
  }

  private void insertCatalog(Catalog catalog, Connection connection) {
    queryExecutor.execute(connection, databaseStatements.getInsertCatalogTemplate(), catalog.getId(),
        catalog.getParticipantId(), toJson(catalog.getProperties()), false);
  }

  private void insertDataServices(Catalog catalog, Connection connection) {
    if (catalog.getDataServices() != null) {
      Set<DataService> dataServicesToSave = new HashSet<>(catalog.getDataServices());
      for (DataService dataService : dataServicesToSave) {
        queryExecutor.execute(connection, databaseStatements.getInsertDataServiceTemplate(), dataService.getId(),
            dataService.getEndpointDescription(), dataService.getEndpointUrl());
        queryExecutor.execute(connection, databaseStatements.getInsertCatalogDataServiceTemplate(), catalog.getId(),
            dataService.getId());
      }
    }
  }

  private void insertDatasets(Catalog catalog, Connection connection) throws SQLException {
    if (catalog.getDatasets() != null) {
      for (Dataset dataset : catalog.getDatasets()) {
        dataset.getProperties().put(CoreConstants.EDC_NAMESPACE + "participantId", catalog.getParticipantId());
        queryExecutor.execute(connection, databaseStatements.getInsertDatasetTemplate(), dataset.getId(),
            toJson(dataset.getOffers()), toJson(dataset.getProperties()), catalog.getId());

        insertDistributions(dataset, connection);
      }
    }
  }

  private void insertDistributions(Dataset dataset, Connection connection) throws SQLException {
    if (dataset.getDistributions() != null) {
      for (Distribution distribution : dataset.getDistributions()) {
        DataService dataService = distribution.getDataService();
        if (!dataServiceExists(dataService.getId())) {
          queryExecutor.execute(connection, databaseStatements.getInsertDataServiceTemplate(), dataService.getId(),
              dataService.getEndpointDescription(), dataService.getEndpointUrl());
        }
        queryExecutor.execute(connection, databaseStatements.getInsertDistributionTemplate(), distribution.getFormat(),
            dataService.getId(), dataset.getId());
      }
    }
  }

  private List<Catalog> getPaginatedCatalogs(QuerySpec querySpec, Connection connection) {
    SqlQueryStatement dataSetQueryStatement = databaseStatements.createDatasetQuery(querySpec);
    try (Stream<Dataset> dataSetStream = queryExecutor.query(connection, true, rs -> mapResultSetToDataset(rs, true),
        dataSetQueryStatement.getQueryAsString(), dataSetQueryStatement.getParameters())) {
      List<Dataset> dataSets = dataSetStream.toList();

      Set<Object> catalogIds = dataSets.stream().flatMap(
          d -> d.getProperties().entrySet().stream().filter(e -> INTERNAL_CATALOG_ID.equals(e.getKey()))
              .map(Map.Entry::getValue).collect(Collectors.toSet()).stream()).collect(Collectors.toSet());

      if (catalogIds.isEmpty()) {
        return Collections.emptyList();
      }

      QuerySpec catalogQuerySpec = QuerySpec.Builder.newInstance()
          .filter(new Criterion(databaseStatements.getCatalogIdColumn(), "in", catalogIds)).build();

      SqlQueryStatement catalogQueryStatement = databaseStatements.createQuery(catalogQuerySpec);
      try (Stream<Catalog.Builder> catalogStream = queryExecutor.query(connection, true,
          this::mapResultSetToCatalogNoDependencies, catalogQueryStatement.getQueryAsString(),
          catalogQueryStatement.getParameters())) {
        return getCatalogs(catalogStream, dataSets);
      }
    }
  }

  private List<Catalog> getCatalogs(Stream<Catalog.Builder> catalogStream, List<Dataset> dataSets) {
    List<Catalog.Builder> catalogs = catalogStream.toList();
    List<Catalog> result = new ArrayList<>();

    List<String> dataSetIds = dataSets.stream().map(Dataset::getId).toList();
    if (dataSetIds.isEmpty()) {
      return result;
    }
    catalogs.forEach(catalog -> {
      String catalogId = catalog.build().getId();
      List<Dataset> datasetFiltered = dataSets.stream()
          .filter(dataset -> catalogId.equals(dataset.getProperties().get(INTERNAL_CATALOG_ID))).toList();
      datasetFiltered.forEach(dataset -> dataset.getProperties().remove(INTERNAL_CATALOG_ID));
      List<DataService> dataServicesForCatalog = getDataServicesForCatalog(catalogId);
      catalog.datasets(datasetFiltered);
      catalog.dataServices(dataServicesForCatalog);
      result.add(catalog.build());
    });

    return result;
  }

}
