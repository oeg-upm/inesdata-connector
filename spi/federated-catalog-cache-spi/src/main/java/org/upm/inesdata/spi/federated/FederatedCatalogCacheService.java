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

  ServiceResult<Collection<Catalog>> searchPagination(QuerySpec querySpec);
}
 