package org.upm.inesdata.countelements.service;

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.upm.inesdata.spi.countelements.domain.CountElement;
import org.upm.inesdata.spi.countelements.index.CountElementsIndex;
import org.upm.inesdata.spi.countelements.service.CountElementsService;

public class CountElementsServiceImpl implements CountElementsService {
    private final CountElementsIndex countElementsIndex;
    private final TransactionContext transactionContext;

    public CountElementsServiceImpl(CountElementsIndex countElementsIndex, TransactionContext transactionContext) {
        this.countElementsIndex = countElementsIndex;
        this.transactionContext = transactionContext;
    }

    @Override
    public CountElement countElements(String entityType, QuerySpec querySpec) {
        return transactionContext.execute(() -> countElementsIndex.countElements(entityType, querySpec));
    }
}
