package org.upm.inesdata.vocabulary.sql.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.StoreResult;
import org.eclipse.edc.sql.QueryExecutor;
import org.eclipse.edc.sql.store.AbstractSqlStore;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.jetbrains.annotations.Nullable;
import org.upm.inesdata.spi.vocabulary.VocabularyIndex;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;
import org.upm.inesdata.vocabulary.sql.index.schema.VocabularyStatements;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.eclipse.edc.spi.query.Criterion.criterion;

/**
 * Implementation of the VocabularyIndex with SQL databases
 */
public class SqlVocabularyIndex extends AbstractSqlStore implements VocabularyIndex {

    private final VocabularyStatements vocabularyStatements;

    public SqlVocabularyIndex(DataSourceRegistry dataSourceRegistry, 
                              String dataSourceName, 
                              TransactionContext transactionContext,
                              ObjectMapper objectMapper, 
                              VocabularyStatements vocabularyStatements, 
                              QueryExecutor queryExecutor) {
        super(dataSourceRegistry, dataSourceName, transactionContext, objectMapper, queryExecutor);
        this.vocabularyStatements = Objects.requireNonNull(vocabularyStatements);
    }

    @Override
    public Stream<Vocabulary> allVocabularies() {
        return transactionContext.execute(() -> {
            try {
                var statement = vocabularyStatements.createQuery(new QuerySpec());
                return queryExecutor.query(getConnection(), true, this::mapVocabulary, statement.getQueryAsString(), statement.getParameters());
            } catch (SQLException e) {
                throw new EdcPersistenceException(e);
            }
        });
    }

    @Override
    public @Nullable Vocabulary findById(String vocabularyId) {
        Objects.requireNonNull(vocabularyId);

        try (var connection = getConnection()) {
            var querySpec = QuerySpec.Builder.newInstance().filter(criterion("id", "=", vocabularyId)).build();
            var statement = vocabularyStatements.createQuery(querySpec);
            return queryExecutor.query(connection, true, this::mapVocabulary, statement.getQueryAsString(), statement.getParameters())
                    .findFirst().orElse(null);
        } catch (SQLException e) {
            throw new EdcPersistenceException(e);
        }
    }

    @Override
    public StoreResult<Void> create(Vocabulary vocabulary) {
        Objects.requireNonNull(vocabulary);

        var vocabularyId = vocabulary.getId();
        var connectorId = vocabulary.getConnectorId();
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                if (existsByIdAndConnectorId(vocabularyId, connectorId, connection)) {
                    var msg = format(VocabularyIndex.VOCABULARY_EXISTS_TEMPLATE, vocabularyId);
                    return StoreResult.alreadyExists(msg);
                }

                queryExecutor.execute(connection, vocabularyStatements.getInsertVocabularyTemplate(),
                        vocabularyId,
                        vocabulary.getCreatedAt(),
                        vocabulary.getName(),
                        vocabulary.getConnectorId(),
                        vocabulary.getCategory(),
                        toJson(vocabulary.getJsonSchema())
                );

                return StoreResult.success();
            } catch (Exception e) {
                throw new EdcPersistenceException(e);
            }
        });
    }

    @Override
    public StoreResult<Vocabulary> deleteById(String vocabularyId) {
        Objects.requireNonNull(vocabularyId);

        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                var vocabulary = findById(vocabularyId);
                if (vocabulary == null) {
                    return StoreResult.notFound(format(VocabularyIndex.VOCABULARY_NOT_FOUND_TEMPLATE, vocabularyId));
                }

                queryExecutor.execute(connection, vocabularyStatements.getDeleteVocabularyByIdTemplate(), vocabularyId);

                return StoreResult.success(vocabulary);
            } catch (Exception e) {
                throw new EdcPersistenceException(e.getMessage(), e);
            }
        });
    }

    @Override
    public StoreResult<Vocabulary> updateVocabulary(Vocabulary vocabulary) {
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                var vocabularyId = vocabulary.getId();
                var connectorId = vocabulary.getConnectorId();
                if (existsByIdAndConnectorId(vocabularyId, connectorId, connection)) {
                    queryExecutor.execute(connection, vocabularyStatements.getUpdateVocabularyTemplate(),
                            vocabulary.getName(),
                            toJson(vocabulary.getJsonSchema()),
                            vocabulary.getCategory(),
                            vocabularyId
                    );

                    return StoreResult.success(vocabulary);
                }
                return StoreResult.notFound(format(VocabularyIndex.VOCABULARY_NOT_FOUND_TEMPLATE, vocabularyId));

            } catch (Exception e) {
                throw new EdcPersistenceException(e);
            }
        });
    }

    @Override
    public Stream<Vocabulary> searchVocabulariesByConnector(String connectorId) {
        return transactionContext.execute(() -> {
            try {
                var querySpec = QuerySpec.Builder.newInstance().filter(criterion("connectorId", "=", connectorId)).build();
                var statement = vocabularyStatements.createQuery(querySpec);
                return queryExecutor.query(getConnection(), true, this::mapVocabulary, statement.getQueryAsString(), statement.getParameters());
            } catch (SQLException e) {
                throw new EdcPersistenceException(e);
            }
        });
    }

    @Override
    public StoreResult<Vocabulary> deleteByIdAndConnectorId(String vocabularyId, String connectorId) {
        Objects.requireNonNull(vocabularyId);

        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                var vocabulary = findByIdAndConnectorId(vocabularyId, connectorId);
                if (vocabulary == null) {
                    return StoreResult.notFound(format(VocabularyIndex.VOCABULARY_NOT_FOUND_TEMPLATE, vocabularyId));
                }

                queryExecutor.execute(connection, vocabularyStatements.getDeleteVocabularyByIdAndConnectorIdTemplate(), vocabularyId, connectorId);

                return StoreResult.success(vocabulary);
            } catch (Exception e) {
                throw new EdcPersistenceException(e.getMessage(), e);
            }
        });
    }

    @Override
    public @Nullable Vocabulary findByIdAndConnectorId(String vocabularyId, String connectorId) {
        Objects.requireNonNull(vocabularyId);
        Objects.requireNonNull(connectorId);

        try (var connection = getConnection()) {
            var querySpec = QuerySpec.Builder.newInstance().filter(criterion("id", "=", vocabularyId)).filter(criterion("connectorId", "=", connectorId)).build();
            var statement = vocabularyStatements.createQuery(querySpec);
            return queryExecutor.query(connection, true, this::mapVocabulary, statement.getQueryAsString(), statement.getParameters())
                    .findFirst().orElse(null);
        } catch (SQLException e) {
            throw new EdcPersistenceException(e);
        }
    }

    public StoreResult<Void> deleteByConnectorId(String connectorId) {
        Objects.requireNonNull(connectorId);

        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                queryExecutor.execute(connection, vocabularyStatements.getDeleteVocabulariesByConnectorIdTemplate(), connectorId);
                return StoreResult.success();
            } catch (Exception e) {
                throw new EdcPersistenceException(e.getMessage(), e);
            }
        });
    }

    private int mapRowCount(ResultSet resultSet) throws SQLException {
        return resultSet.getInt(vocabularyStatements.getCountVariableName());
    }

    private boolean existsByIdAndConnectorId(String vocabularyId, String connectorId, Connection connection) {
        var sql = vocabularyStatements.getCountVocabularyByIdAndConnectorIdClause();
        try (var stream = queryExecutor.query(connection, false, this::mapRowCount, sql, vocabularyId, connectorId)) {
            return stream.findFirst().orElse(0) > 0;
        }
    }

    private Vocabulary mapVocabulary(ResultSet resultSet) throws SQLException {
        return Vocabulary.Builder.newInstance()
                .id(resultSet.getString(vocabularyStatements.getVocabularyIdColumn()))
                .createdAt(resultSet.getLong(vocabularyStatements.getCreatedAtColumn()))
                .name(resultSet.getString(vocabularyStatements.getNameColumn()))
                .connectorId(resultSet.getString(vocabularyStatements.getConnectorIdColumn()))
                .category(resultSet.getString(vocabularyStatements.getCategoryColumn()))
                .jsonSchema(resultSet.getString(vocabularyStatements.getJsonSchemaColumn()))
                .build();
    }


}
