package org.upm.inesdata.spi.countelements.index;

import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;
import org.eclipse.edc.spi.query.QuerySpec;
import org.upm.inesdata.spi.countelements.domain.CountElement;

/**
 * Query interface for counting elements.
 **/
@ExtensionPoint
public interface CountElementsIndex {

    /**
     * Counts all contract agreements
     *
     * @param entityType entity type
     * @param querySpec filters
     * @return the number of contract agreements
     */
    CountElement countElements(String entityType, QuerySpec querySpec);
}
