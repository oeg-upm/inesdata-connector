package org.upm.inesdata.countelements.sql.index;

import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.sql.QueryExecutor;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.upm.inesdata.countelements.sql.index.schema.CountElementsStatements;
import org.upm.inesdata.countelements.sql.index.schema.postgres.PostgresDialectStatements;
import org.upm.inesdata.spi.countelements.index.CountElementsIndex;

/**
 * Extension that counts elements in SQL databases
 */
@Provides({CountElementsIndex.class})
@Extension(value = "SQL count elements index")
public class SqlCountElementsIndexServiceExtension implements ServiceExtension {

    /**
     * Name of the vocabulary datasource.
     */
    @Setting(required = true)
    public static final String DATASOURCE_SETTING_NAME = "edc.datasource.countelements.name";

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Inject
    private TransactionContext transactionContext;

    @Inject(required = false)
    private CountElementsStatements dialect;

    @Inject
    private TypeManager typeManager;

    @Inject
    private QueryExecutor queryExecutor;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var dataSourceName = context.getConfig().getString(DATASOURCE_SETTING_NAME, DataSourceRegistry.DEFAULT_DATASOURCE);

        var sqlCountElementsLoader = new SqlCountElementsIndex(dataSourceRegistry, dataSourceName, transactionContext, typeManager.getMapper(),
                getDialect(), queryExecutor);

        context.registerService(CountElementsIndex.class, sqlCountElementsLoader);
    }

    private CountElementsStatements getDialect() {
        return dialect != null ? dialect : new PostgresDialectStatements();
    }
}
