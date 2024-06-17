package org.upm.inesdata.spi.federated.index;

import org.eclipse.edc.catalog.spi.FederatedCacheStore;
import org.eclipse.edc.connector.controlplane.catalog.spi.Catalog;
import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;
import org.eclipse.edc.spi.query.QuerySpec;

import java.util.Collection;

/**
 * Datastore interface for objects.
 */
@ExtensionPoint
public interface FederatedCacheStoreIndex extends FederatedCacheStore {

  /**
   * Queries the store for {@code ContractOffer}s
   *
   * @param querySpec A list of criteria and pagination the dataset must fulfill
   * @return A collection of catalog that are already in the store and that satisfy a given list of criteria and pagination.
   */
  Collection<Catalog> queryPagination(QuerySpec querySpec);

}
