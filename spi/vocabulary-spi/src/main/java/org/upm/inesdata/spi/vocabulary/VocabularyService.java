/*
 * INESData - UPM
 */
package org.upm.inesdata.spi.vocabulary;

import org.eclipse.edc.spi.result.ServiceResult;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;

import java.util.List;

/**
 * Service interface for {@link Vocabulary} objects.
 */
public interface VocabularyService {

    /**
     * Returns a vocabulary by its id
     *
     * @param vocabularyId id of the vocabulary
     * @return the vocabulary, null if it's not found
     */
    Vocabulary findById(String vocabularyId);

    /**
     * Get Vocabularys
     *
     * @return the collection of vocabularys stored
     */
    ServiceResult<List<Vocabulary>> search();

    /**
     * Create a vocabulary
     *
     * @param vocabulary the vocabulary
     * @return successful result if the vocabulary is created correctly, failure otherwise
     */
    ServiceResult<Vocabulary> create(Vocabulary vocabulary);

    /**
     * Delete a vocabulary
     *
     * @param vocabularyId  the id of the vocabulary to be deleted
     * @param participantId the participant id of the connector
     * @return successful result if the vocabulary is deleted correctly, failure otherwise
     */
    ServiceResult<Vocabulary> delete(String vocabularyId, String participantId);

    /**
     * Updates a vocabulary. If the vocabulary does not yet exist, {@link ServiceResult#notFound(String)} will be returned.
     *
     * @param vocabulary    The content of the Vocabulary. Note that {@link Vocabulary#getId()} will be ignored, rather the separately supplied ID is used
     * @param participantId the participant id of the connector
     * @return successful if updated, a failure otherwise.
     */
    ServiceResult<Vocabulary> update(Vocabulary vocabulary, String participantId);

}
 