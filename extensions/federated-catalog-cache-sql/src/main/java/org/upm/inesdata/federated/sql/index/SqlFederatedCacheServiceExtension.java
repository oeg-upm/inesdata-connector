package org.upm.inesdata.federated.sql.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.catalog.spi.FederatedCatalogCache;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.sql.QueryExecutor;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.upm.inesdata.federated.sql.index.schema.SqlFederatedCatalogStatements;
import org.upm.inesdata.federated.sql.index.schema.postgres.PostgresDialectStatements;
import org.upm.inesdata.spi.federated.index.PaginatedFederatedCacheStoreIndex;

/**
 * Extension that stores federatedCatalogs in SQL databases
 */
@Provides({ FederatedCatalogCache.class, PaginatedFederatedCacheStoreIndex.class })
@Extension(value = "SQL federatedCatalog index")
public class SqlFederatedCacheServiceExtension implements ServiceExtension {

    /**
     * Name of the federatedCatalog datasource.
     */
    @Setting(required = true)
    public static final String DATASOURCE_SETTING_NAME = "edc.datasource.federatedCatalog.name";

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Inject
    private TransactionContext transactionContext;

    @Inject(required = false)
    private SqlFederatedCatalogStatements dialect;

    @Inject
    private TypeManager typeManager;

    @Inject
    private QueryExecutor queryExecutor;

    /**
     * Provides a defaultCacheStore implementation
     */
    @Provider(isDefault = true)
    public PaginatedFederatedCacheStoreIndex defaultCacheStore() {
        return new SqlFederatedCacheStore(dataSourceRegistry, DATASOURCE_SETTING_NAME,transactionContext,getObjectMapper(),dialect,queryExecutor);
    }

    /**
     * Initializes the service
     */
    @Override
    public void initialize(ServiceExtensionContext context) {
        var dataSourceName = context.getConfig().getString(DATASOURCE_SETTING_NAME, DataSourceRegistry.DEFAULT_DATASOURCE);

        var sqlFederatedCacheStore = new SqlFederatedCacheStore(dataSourceRegistry, dataSourceName, transactionContext, typeManager.getMapper(),
                getDialect(), queryExecutor);

        context.registerService(PaginatedFederatedCacheStoreIndex.class, sqlFederatedCacheStore);
        context.registerService(FederatedCatalogCache.class, sqlFederatedCacheStore);
    }

    private SqlFederatedCatalogStatements getDialect() {
        return dialect != null ? dialect : new PostgresDialectStatements();
    }
    private ObjectMapper getObjectMapper() {
        return typeManager.getMapper();
    }
}
