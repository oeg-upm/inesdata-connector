package org.upm.inesdata.spi.federated;

import org.eclipse.edc.connector.controlplane.catalog.spi.Catalog;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.ServiceResult;

import java.util.Collection;
import java.util.List;

/**
 * Service interface for {@link org.eclipse.edc.connector.controlplane.catalog.spi.Catalog} objects.
 */
public interface FederatedCatalogCacheService {

  /**
   * Searches for catalogs based on the given query specification and returns a paginated result.
   *
   * @param querySpec the specification of the query which includes filters, sorting, and pagination details.
   * @return a ServiceResult containing a collection of catalogs that match the query criteria.
   */
  ServiceResult<Collection<Catalog>> searchPagination(QuerySpec querySpec);
}
 