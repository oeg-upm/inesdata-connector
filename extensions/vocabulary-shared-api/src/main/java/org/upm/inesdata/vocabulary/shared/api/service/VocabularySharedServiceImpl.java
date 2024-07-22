package org.upm.inesdata.vocabulary.shared.api.service;

import org.eclipse.edc.spi.result.ServiceResult;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.upm.inesdata.spi.vocabulary.VocabularyIndex;
import org.upm.inesdata.spi.vocabulary.VocabularySharedService;
import org.upm.inesdata.spi.vocabulary.domain.ConnectorVocabulary;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;
import org.upm.inesdata.vocabulary.service.VocabularyServiceImpl;

import java.util.List;

/**
 * Implementation of the {@link VocabularySharedService} interface
 */
public class VocabularySharedServiceImpl extends VocabularyServiceImpl implements VocabularySharedService {

    /**
     * Constructor
     */
    public VocabularySharedServiceImpl(VocabularyIndex index, TransactionContext transactionContext) {
        super(index, transactionContext);
    }

    @Override
    public ServiceResult<List<Vocabulary>> searchVocabulariesByConnector(ConnectorVocabulary connectorVocabulary) {
        return transactionContext.execute(() -> {
            try (var stream = index.searchVocabulariesByConnector(connectorVocabulary.getConnectorId())) {
                return ServiceResult.success(stream.toList());
            }
        });
    }
}
