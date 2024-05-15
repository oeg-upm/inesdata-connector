package org.upm.inesdata.vocabulary.sql.index;

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
import org.upm.inesdata.spi.vocabulary.VocabularyIndex;
import org.upm.inesdata.vocabulary.sql.index.schema.VocabularyStatements;
import org.upm.inesdata.vocabulary.sql.index.schema.postgres.PostgresDialectStatements;

/**
 * Extension that stores vocabylaries in SQL databases
 */
@Provides({ VocabularyIndex.class })
@Extension(value = "SQL vocabulary index")
public class SqlVocabularyIndexServiceExtension implements ServiceExtension {

    /**
     * Name of the vocabulary datasource.
     */
    @Setting(required = true)
    public static final String DATASOURCE_SETTING_NAME = "edc.datasource.vocabulary.name";

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Inject
    private TransactionContext transactionContext;

    @Inject(required = false)
    private VocabularyStatements dialect;

    @Inject
    private TypeManager typeManager;

    @Inject
    private QueryExecutor queryExecutor;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var dataSourceName = context.getConfig().getString(DATASOURCE_SETTING_NAME, DataSourceRegistry.DEFAULT_DATASOURCE);

        var sqlVocabularyLoader = new SqlVocabularyIndex(dataSourceRegistry, dataSourceName, transactionContext, typeManager.getMapper(), 
                getDialect(), queryExecutor);

        context.registerService(VocabularyIndex.class, sqlVocabularyLoader);
    }

    private VocabularyStatements getDialect() {
        return dialect != null ? dialect : new PostgresDialectStatements();
    }
}
