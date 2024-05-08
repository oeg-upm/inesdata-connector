package org.upm.inesdata.vocabulary.service;

import org.eclipse.edc.spi.result.ServiceResult;
import org.eclipse.edc.transaction.spi.TransactionContext;

import org.upm.inesdata.spi.vocabulary.VocabularyIndex;
import org.upm.inesdata.spi.vocabulary.VocabularyService;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;

import java.util.List;

/**
 * Implementation of the {@link VocabularyService} interface
 */
public class VocabularyServiceImpl implements VocabularyService {

    private final VocabularyIndex index;
    private final TransactionContext transactionContext;

    /**
     * Constructor
     */
    public VocabularyServiceImpl(VocabularyIndex index, TransactionContext transactionContext) {
        this.index = index;

        this.transactionContext = transactionContext;
    }

    @Override
    public Vocabulary findById(String vocabularyId) {
        return transactionContext.execute(() -> index.findById(vocabularyId));
    }

    @Override
    public ServiceResult<List<Vocabulary>> search() {
        return transactionContext.execute(() -> {
            try (var stream = index.allVocabularies()) {
                return ServiceResult.success(stream.toList());
            }
        });
    }

    @Override
    public ServiceResult<Vocabulary> create(Vocabulary vocabulary) {
        return transactionContext.execute(() -> {
            var createResult = index.create(vocabulary);
            if (createResult.succeeded()) {
                return ServiceResult.success(vocabulary);
            }
            return ServiceResult.fromFailure(createResult);
        });
    }

    @Override
    public ServiceResult<Vocabulary> delete(String vocabularyId) {
        return transactionContext.execute(() -> {
            var deleted = index.deleteById(vocabularyId);
            return ServiceResult.from(deleted);
        });
    }

    @Override
    public ServiceResult<Vocabulary> update(Vocabulary vocabulary) {
        return transactionContext.execute(() -> {
            var updatedVocabulary = index.updateVocabulary(vocabulary);
            return ServiceResult.from(updatedVocabulary);
        });
    }

}
