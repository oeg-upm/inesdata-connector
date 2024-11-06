package org.upm.inesdata.spi.vocabulary;

import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.result.StoreResult;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;

import java.util.stream.Stream;

/**
 * Datastore interface for {@link Vocabulary} objects.
 */
@ExtensionPoint
public interface VocabularyIndex {

    String VOCABULARY_EXISTS_TEMPLATE = "Vocabulary with ID %s already exists";
    String VOCABULARY_NOT_FOUND_TEMPLATE = "Vocabulary with ID %s not found";

    /**
     * Finds all stored vocabularies
     *
     * @return A potentially empty collection of {@link Vocabulary}, never null.
     */
    Stream<Vocabulary> allVocabularies();

    /**
     * Fetches the {@link Vocabulary} with the given ID from the metadata backend.
     *
     * @param vocabularyId A String that represents the Vocabulary ID, in most cases this will be a UUID.
     * @return The {@link Vocabulary} if one was found, or null otherwise.
     * @throws NullPointerException If {@code vocabularyId} was null or empty.
     */
    Vocabulary findById(String vocabularyId);

    /**
     * Stores a {@link Vocabulary} in the vocabulary index, if no vocabulary with the same ID already exists.
     * Implementors must ensure that it's stored in a transactional way.
     *
     * @param vocabulary The {@link Vocabulary} to store
     * @return {@link StoreResult#success()} if the objects were stored, {@link StoreResult#alreadyExists(String)} when an object with the same ID already exists.
     */
    StoreResult<Void> create(Vocabulary vocabulary);

    /**
     * Deletes a vocabulary if it exists.
     *
     * @param vocabularyId Id of the vocabulary to be deleted.
     * @return {@link StoreResult#success(Object)} if the object was deleted, {@link StoreResult#notFound(String)} when an object with that ID was not found.
     * @throws EdcPersistenceException if something goes wrong.
     */
    StoreResult<Vocabulary> deleteById(String vocabularyId);

    /**
     * Updates a vocabulary with the content from the given {@link Vocabulary}. If the vocabulary is not found, no further database interaction takes place.
     *
     * @param vocabulary The Vocabulary containing the new values. ID will be ignored.
     * @return {@link StoreResult#success(Object)} if the object was updated, {@link StoreResult#notFound(String)} when an object with that ID was not found.
     */
    StoreResult<Vocabulary> updateVocabulary(Vocabulary vocabulary);

    /**
     * Finds all stored vocabularies from a connector
     *
     * @param connectorId the connector ID
     * @return A potentially empty collection of {@link Vocabulary}, never null.
     */
    Stream<Vocabulary> searchVocabulariesByConnector(String connectorId);

    /**
     * Deletes vocabularies by connectorId.
     *
     * @param connectorId Id of the connector
     * @return {@link StoreResult#success(Object)} if the object was deleted, {@link StoreResult#notFound(String)} when an object with that ID was not found.
     * @throws EdcPersistenceException if something goes wrong.
     */
    StoreResult<Void> deleteByConnectorId(String connectorId);

    /**
     * Fetches the {@link Vocabulary} with the given ID from the metadata backend.
     *
     * @param vocabularyId A String that represents the Vocabulary ID, in most cases this will be a UUID.
     * @param connectorId  Id of the connector
     * @return The {@link Vocabulary} if one was found, or null otherwise.
     * @throws NullPointerException If {@code vocabularyId} was null or empty.
     */
    Vocabulary findByIdAndConnectorId(String vocabularyId, String connectorId);

    /**
     * Deletes a vocabulary if it exists.
     *
     * @param vocabularyId Id of the vocabulary to be deleted.
     * @param connectorId  Id of the connector
     * @return {@link StoreResult#success(Object)} if the object was deleted, {@link StoreResult#notFound(String)} when an object with that ID was not found.
     * @throws EdcPersistenceException if something goes wrong.
     */
    StoreResult<Vocabulary> deleteByIdAndConnectorId(String vocabularyId, String connectorId);

}
