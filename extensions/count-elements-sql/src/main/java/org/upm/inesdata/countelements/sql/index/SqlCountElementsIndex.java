package org.upm.inesdata.countelements.sql.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.QueryExecutor;
import org.eclipse.edc.sql.store.AbstractSqlStore;
import org.eclipse.edc.sql.translation.SqlQueryStatement;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.upm.inesdata.countelements.sql.index.schema.CountElementsStatements;
import org.upm.inesdata.spi.countelements.domain.CountElement;
import org.upm.inesdata.spi.countelements.index.CountElementsIndex;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Implementation of the CountElementsIndex with SQL databases
 */
public class SqlCountElementsIndex extends AbstractSqlStore implements CountElementsIndex {

    private final CountElementsStatements countElementsStatements;

    public SqlCountElementsIndex(DataSourceRegistry dataSourceRegistry,
                                 String dataSourceName,
                                 TransactionContext transactionContext,
                                 ObjectMapper objectMapper,
                                 CountElementsStatements countElementsStatements,
                                 QueryExecutor queryExecutor) {
        super(dataSourceRegistry, dataSourceName, transactionContext, objectMapper, queryExecutor);
        this.countElementsStatements = Objects.requireNonNull(countElementsStatements);
    }

    @Override
    public CountElement countElements(String entityType, QuerySpec querySpec) {
        try (var connection = getConnection()) {
            long count;
            if ("federatedCatalog".equals(entityType)) {
                SqlQueryStatement dataSetQueryStatement = countElementsStatements.createCountDatasetQuery(entityType, querySpec);
                count = queryExecutor.single(connection, true, r -> r.getLong(1),
                        dataSetQueryStatement.getQueryAsString(), dataSetQueryStatement.getParameters());
            } else {
                var sql = countElementsStatements.getCount(entityType);
                count = queryExecutor.single(connection, true, r -> r.getLong(1), sql);
            }

            return CountElement.Builder.newInstance().count(count).build();
        } catch (SQLException e) {
            throw new EdcPersistenceException(e);
        }

    }

}
