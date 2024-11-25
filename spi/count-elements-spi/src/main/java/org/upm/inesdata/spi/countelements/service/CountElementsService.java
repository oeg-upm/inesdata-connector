package org.upm.inesdata.spi.countelements.service;

import org.eclipse.edc.spi.query.QuerySpec;
import org.upm.inesdata.spi.countelements.domain.CountElement;

/**
 * Service interface for getting the total number of elements of an entity.
 */
public interface CountElementsService {

    /**
     * Gets the total number of elements of an entity.
     *
     * @param entityType entity type
     * @param querySpec filters
     * @return the total number of elements
     */
    CountElement countElements(String entityType, QuerySpec querySpec);
}
