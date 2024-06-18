package org.upm.inesdata.spi.countelements.index;

import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;
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
     * @return the number of contract agreements
     */
    CountElement countElements(String entityType);
}
