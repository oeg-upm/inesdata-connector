package org.upm.inesdata.federated.sql.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.catalog.spi.FederatedCacheStore;
import org.eclipse.edc.connector.controlplane.catalog.spi.Catalog;
import org.eclipse.edc.connector.controlplane.catalog.spi.DataService;
import org.eclipse.edc.connector.controlplane.catalog.spi.Dataset;
import org.eclipse.edc.connector.controlplane.catalog.spi.Distribution;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.StoreResult;
import org.eclipse.edc.sql.QueryExecutor;
import org.eclipse.edc.sql.store.AbstractSqlStore;
import org.eclipse.edc.sql.translation.SqlQueryStatement;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.upm.inesdata.federated.sql.index.schema.SqlFederatedCatalogStatements;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SqlFederatedCacheStore extends AbstractSqlStore implements FederatedCacheStore {

    private final SqlFederatedCatalogStatements databaseStatements;

    public SqlFederatedCacheStore(DataSourceRegistry dataSourceRegistry,
        String dataSourceName,
        TransactionContext transactionContext,
        ObjectMapper objectMapper,
        SqlFederatedCatalogStatements databaseStatements,
        QueryExecutor queryExecutor) {
        super(dataSourceRegistry, dataSourceName, transactionContext, objectMapper, queryExecutor);
        this.databaseStatements = Objects.requireNonNull(databaseStatements);
    }
    @Override
    public void save(Catalog catalog) {
        Objects.requireNonNull(catalog);

        transactionContext.execute(() -> {
            try (var connection = getConnection()) {
//                connection.setAutoCommit(false);

                // Eliminar toda la información relacionada con el catálogo
                deleteRelatedCatalogData(connection, catalog.getId());

                queryExecutor.execute(connection, databaseStatements.getInsertCatalogTemplate(),
                    catalog.getId(),
                    catalog.getParticipantId(),
                    toJson(catalog.getProperties()),
                    false
                );

                if (catalog.getDataServices() != null) {
                    for (DataService dataService : catalog.getDataServices()) {
                        queryExecutor.execute(connection, databaseStatements.getInsertDataServiceTemplate(),
                            dataService.getId(),
                            dataService.getEndpointDescription(),
                            dataService.getEndpointUrl()
                        );
                        queryExecutor.execute(connection, databaseStatements.getInsertCatalogDataServiceTemplate(),
                            catalog.getId(),
                            dataService.getId()
                        );
                    }
                }

                if (catalog.getDatasets() != null) {
                    for (Dataset dataset : catalog.getDatasets()) {
                        queryExecutor.execute(connection, databaseStatements.getInsertDatasetTemplate(),
                            dataset.getId(),
                            toJson(dataset.getOffers()),
                            toJson(dataset.getProperties()),
                            catalog.getId()
                        );

                        if (dataset.getDistributions() != null) {
                            for (Distribution distribution : dataset.getDistributions()) {
                                DataService dataService = distribution.getDataService();
                                if (!dataServiceExists(dataService.getId())) {
                                    queryExecutor.execute(connection, databaseStatements.getInsertDataServiceTemplate(),
                                        dataService.getId(),
                                        dataService.getEndpointDescription(),
                                        dataService.getEndpointUrl()
                                    );
                                }
                                queryExecutor.execute(connection, databaseStatements.getInsertDistributionTemplate(),
                                    distribution.getFormat(),
                                    dataService.getId(),
                                    dataset.getId()
                                );
                            }
                        }
                    }
                }


                return StoreResult.success();
            } catch (Exception e) {
                throw new EdcPersistenceException(e);
            }
        });
    }


    @Override
    public Collection<Catalog> query(List<Criterion> query) {
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                QuerySpec querySpec = QuerySpec.Builder.newInstance().filter(query).build();
                SqlQueryStatement queryStatement = databaseStatements.createQuery(querySpec);
                return queryExecutor.query(connection, true, this::mapResultSetToCatalog, queryStatement.getQueryAsString(), queryStatement.getParameters()).collect(Collectors.toList());
            } catch (SQLException e) {
                throw new EdcPersistenceException(e);
            }
        });
    }




    @Override
    public void deleteExpired() {
        transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                queryExecutor.execute(connection, databaseStatements.getDeleteExpiredCatalogsTemplate(), true);
                return null;
            } catch (SQLException e) {
                throw new EdcPersistenceException(e);
            }
        });
    }

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


    private void deleteRelatedCatalogData(Connection connection, String catalogId) throws SQLException {
        // Eliminar las distribuciones relacionadas con los datasets del catálogo
        String deleteDistributionsSql = databaseStatements.getDeleteDistributionsForCatalogTemplate();
        queryExecutor.execute(connection, deleteDistributionsSql, catalogId);

        // Eliminar las relaciones entre el catálogo y los servicios de datos
        String deleteCatalogDataServicesSql = databaseStatements.getDeleteCatalogDataServicesTemplate();
        queryExecutor.execute(connection, deleteCatalogDataServicesSql, catalogId);

        // Eliminar los servicios de datos que no están relacionados con ningún catálogo o distribución
        String deleteOrphanDataServicesSql = databaseStatements.getDeleteOrphanDataServicesTemplate();
        queryExecutor.execute(connection, deleteOrphanDataServicesSql);

        // Eliminar los datasets del catálogo
        String deleteDatasetsSql = databaseStatements.getDeleteDatasetsForCatalogTemplate();
        queryExecutor.execute(connection, deleteDatasetsSql, catalogId);

        // Eliminar el propio catálogo
        String deleteCatalogSql = databaseStatements.getDeleteCatalogByIdTemplate();
        queryExecutor.execute(connection, deleteCatalogSql, catalogId);
    }

    private boolean dataServiceExists(String dataServiceId) throws SQLException {
        return getDataServiceById(dataServiceId)!=null;
    }

    private Catalog mapResultSetToCatalog(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String participantId = resultSet.getString("participant_id");
        Map<String, Object> properties = fromJson(resultSet.getString("properties"), Map.class);
        List<Dataset> datasets = getDatasetsForCatalog(id);
        List<DataService> dataServices = getDataServicesForCatalog(id);

        return Catalog.Builder.newInstance()
            .id(id)
            .participantId(participantId)
            .datasets(datasets)
            .dataServices(dataServices)
            .properties(properties)
            .build();
    }

    private List<Dataset> getDatasetsForCatalog(String catalogId) {
        try (var connection = getConnection()) {
            String sql = databaseStatements.getSelectDatasetsForCatalogTemplate();
            return queryExecutor.query(connection, false, this::mapResultSetToDataset, sql, catalogId).collect(Collectors.toList());
        } catch (SQLException e) {
            throw new EdcPersistenceException(e);
        }
    }

    private Dataset mapResultSetToDataset(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        Map<String, Object> properties = fromJson(resultSet.getString("properties"), Map.class);
        Map<String, Policy> offers = fromJson(resultSet.getString("offers"), Map.class);

        List<Distribution> distributions = getDistributionsForDataset(id);

        Dataset.Builder datasetBuilder = Dataset.Builder.newInstance()
            .id(id)
            .properties(properties)
            .distributions(distributions);

        if (offers != null) {
            offers.forEach(datasetBuilder::offer);
        }

        return datasetBuilder.build();
    }

    private List<Distribution> getDistributionsForDataset(String datasetId) {
        try (var connection = getConnection()) {
            String sql = databaseStatements.getSelectDistributionsForDatasetTemplate();
            return queryExecutor.query(connection, false, this::mapResultSetToDistribution, sql, datasetId).collect(Collectors.toList());
        } catch (SQLException e) {
            throw new EdcPersistenceException(e);
        }
    }

    private Distribution mapResultSetToDistribution(ResultSet resultSet) throws SQLException {
        String format = resultSet.getString("format");
        String dataServiceId = resultSet.getString("data_service_id");

        DataService dataService = getDataServiceById(dataServiceId);

        return Distribution.Builder.newInstance()
            .format(format)
            .dataService(dataService)
            .build();
    }

    private DataService getDataServiceById(String dataServiceId) {
        try (var connection = getConnection()) {
            String sql = databaseStatements.getSelectDataServicesForIdTemplate();
            return queryExecutor.query(connection, false, this::mapResultSetToDataService, sql, dataServiceId).findFirst().orElse(null);
        } catch (SQLException e) {
            throw new EdcPersistenceException(e);
        }
    }

    private List<DataService> getDataServicesForCatalog(String catalogId) {
        try (var connection = getConnection()) {
            String sql = databaseStatements.getSelectDataServicesForCatalogTemplate();
            return queryExecutor.query(connection, false, this::mapResultSetToDataService, sql, catalogId).collect(
                Collectors.toList());
        } catch (SQLException e) {
            throw new EdcPersistenceException(e);
        }
    }

    private DataService mapResultSetToDataService(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String terms = resultSet.getString("terms");
        String endpointUrl = resultSet.getString("endpoint_url");

        return DataService.Builder.newInstance()
            .id(id)
            .endpointDescription(terms)
            .endpointUrl(endpointUrl)
            .build();
    }
}
