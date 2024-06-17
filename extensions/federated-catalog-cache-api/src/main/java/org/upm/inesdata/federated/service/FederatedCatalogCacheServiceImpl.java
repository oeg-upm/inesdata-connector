package org.upm.inesdata.federated.service;

import org.eclipse.edc.connector.controlplane.catalog.spi.Catalog;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.ServiceResult;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.upm.inesdata.spi.federated.FederatedCatalogCacheService;
import org.upm.inesdata.spi.federated.index.PaginatedFederatedCacheStoreIndex;

import java.util.Collection;

/**
 * Implementation of the {@link FederatedCatalogCacheService} interface
 */
public class FederatedCatalogCacheServiceImpl implements FederatedCatalogCacheService {

    private final PaginatedFederatedCacheStoreIndex index;
    private final TransactionContext transactionContext;

    /**
     * Constructor
     */
    public FederatedCatalogCacheServiceImpl(PaginatedFederatedCacheStoreIndex index, TransactionContext transactionContext) {
        this.index = index;

        this.transactionContext = transactionContext;
    }
    @Override
    public ServiceResult<Collection<Catalog>> searchPagination(QuerySpec querySpec) {
        return transactionContext.execute(() -> ServiceResult.success(index.queryPagination(querySpec)));
    }
}
