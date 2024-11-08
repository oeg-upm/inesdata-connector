package org.upm.inesdata.vocabulary.service;

import org.eclipse.edc.spi.result.ServiceResult;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.eclipse.edc.web.spi.exception.ObjectNotFoundException;
import org.upm.inesdata.spi.vocabulary.VocabularyIndex;
import org.upm.inesdata.spi.vocabulary.VocabularyService;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;

import java.util.List;

/**
 * Implementation of the {@link VocabularyService} interface
 */
public class VocabularyServiceImpl implements VocabularyService {

    protected final VocabularyIndex index;
    protected final TransactionContext transactionContext;

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
            // Create new vocabulary
            var createResult = index.create(vocabulary);

            if (createResult.succeeded()) {
                return ServiceResult.success(vocabulary);
            }
            return ServiceResult.fromFailure(createResult);
        });
    }

    @Override
    public ServiceResult<Vocabulary> delete(String vocabularyId, String participantId) {
        return transactionContext.execute(() -> {
            Vocabulary vocabulary = index.findByIdAndConnectorId(vocabularyId, participantId);
            if (vocabulary == null) {
                throw new ObjectNotFoundException(Vocabulary.class, vocabularyId);
            } else if (!vocabulary.getConnectorId().equals(participantId)) {
                throw new InvalidRequestException("Is not possible to delete a vocabulary for a different connector");
            }

            var deleted = index.deleteByIdAndConnectorId(vocabularyId, participantId);
            return ServiceResult.from(deleted);
        });
    }


    @Override
    public ServiceResult<Vocabulary> update(Vocabulary vocabulary, String participantId) {
        return transactionContext.execute(() -> {
            Vocabulary vocabularyInDB = index.findByIdAndConnectorId(vocabulary.getId(), vocabulary.getConnectorId());
            if (vocabularyInDB == null) {
                throw new ObjectNotFoundException(Vocabulary.class, vocabulary.getId());
            } else if (!vocabularyInDB.getConnectorId().equals(participantId)) {
                throw new InvalidRequestException("Is not possible to delete a vocabulary for a different connector");

            }

            // Update vocabulary
            var updatedVocabulary = index.updateVocabulary(vocabulary);
            return ServiceResult.from(updatedVocabulary);
        });
    }

}
