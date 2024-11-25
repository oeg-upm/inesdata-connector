package org.upm.inesdata.vocabulary.storage;

import org.eclipse.edc.spi.result.StoreResult;
import org.upm.inesdata.spi.vocabulary.VocabularyIndex;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * An ephemeral vocabulary index
 */
public class InMemoryVocabularyIndex implements VocabularyIndex {
    private final Map<String, Vocabulary> cache = new ConcurrentHashMap<>();

    private final ReentrantReadWriteLock lock;

    /**
     * Constructor
     */
    public InMemoryVocabularyIndex() {
        lock = new ReentrantReadWriteLock(true);
    }

    @Override
    public Stream<Vocabulary> allVocabularies() {
        lock.readLock().lock();
        try {
            return cache.values().stream();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Vocabulary findById(String vocabularyId) {
        lock.readLock().lock();
        try {
            return cache.values().stream()
                    .filter(vocabulary -> vocabulary.getId().equals(vocabularyId))
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public StoreResult<Void> create(Vocabulary vocabulary) {
        lock.writeLock().lock();
        try {
            var id = vocabulary.getId();
            if (cache.containsKey(id)) {
                return StoreResult.alreadyExists(format(VOCABULARY_EXISTS_TEMPLATE, id));
            }
            Objects.requireNonNull(vocabulary, "vocabulary");
            Objects.requireNonNull(id, "vocabulary.getId()");
            cache.put(id, vocabulary);
        } finally {
            lock.writeLock().unlock();
        }
        return StoreResult.success();
    }

    @Override
    public StoreResult<Vocabulary> deleteById(String vocabularyId) {
        lock.writeLock().lock();
        try {
            return Optional.ofNullable(delete(vocabularyId))
                    .map(StoreResult::success)
                    .orElse(StoreResult.notFound(format(VOCABULARY_NOT_FOUND_TEMPLATE, vocabularyId)));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public StoreResult<Vocabulary> updateVocabulary(Vocabulary vocabulary) {
        lock.writeLock().lock();
        try {
            var id = vocabulary.getId();
            Objects.requireNonNull(vocabulary, "vocabulary");
            Objects.requireNonNull(id, "vocabularyId");
            if (cache.containsKey(id)) {
                cache.put(id, vocabulary);
                return StoreResult.success(vocabulary);
            }
            return StoreResult.notFound(format(VOCABULARY_NOT_FOUND_TEMPLATE, id));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Stream<Vocabulary> searchVocabulariesByConnector(String connectorId) {
        lock.readLock().lock();
        try {
            return  cache.values().stream()
                    .filter(vocabulary -> vocabulary.getConnectorId().equals(connectorId));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public StoreResult<Void> deleteByConnectorId(String connectorId) {
        lock.writeLock().lock();
        try {
            delete(connectorId);
        } finally {
            lock.writeLock().unlock();
        }

        return StoreResult.success();
    }

    @Override
    public Vocabulary findByIdAndConnectorId(String vocabularyId, String connectorId) {
        lock.readLock().lock();
        try {
            return cache.values().stream()
                    .filter(vocabulary -> vocabulary.getId().equals(vocabularyId) && vocabulary.getConnectorId().equals(connectorId))
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public StoreResult<Vocabulary> deleteByIdAndConnectorId(String vocabularyId, String connectorId) {
        lock.writeLock().lock();
        try {
            var vocabulary = findByIdAndConnectorId(vocabularyId, connectorId);
            if (vocabulary != null) {
                cache.remove(vocabularyId);
                return StoreResult.success(vocabulary);
            } else {
                return StoreResult.notFound(format(VOCABULARY_NOT_FOUND_TEMPLATE, vocabularyId));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Remove a vocabulary from cache based on a key
     */
    private Vocabulary delete(String key) {
        return cache.remove(key);
    }
}
