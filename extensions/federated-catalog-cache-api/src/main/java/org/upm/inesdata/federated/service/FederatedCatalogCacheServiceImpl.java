package org.upm.inesdata.federated.service;

import jakarta.json.JsonObject;
import org.eclipse.edc.connector.controlplane.catalog.spi.Catalog;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.ServiceResult;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.upm.inesdata.federated.controller.FederatedCatalogCacheApi;
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
   * Constructs a FederatedCatalogCacheServiceImpl with the specified dependencies.
   *
   * @param index              the paginated federated cache store index used for managing the catalog cache.
   * @param transactionContext the context for handling transactions.
   */
  public FederatedCatalogCacheServiceImpl(PaginatedFederatedCacheStoreIndex index,
      TransactionContext transactionContext) {
    this.index = index;

    this.transactionContext = transactionContext;
  }

  /**
   * (non-javadoc)
   *
   * @see FederatedCatalogCacheService#searchPagination(QuerySpec)
   */
  @Override
  public ServiceResult<Collection<Catalog>> searchPagination(QuerySpec querySpec) {
    return transactionContext.execute(() -> ServiceResult.success(index.queryPagination(querySpec)));
  }
}
