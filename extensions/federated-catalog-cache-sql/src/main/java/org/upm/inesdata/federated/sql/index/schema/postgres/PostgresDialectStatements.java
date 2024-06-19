package org.upm.inesdata.federated.sql.index.schema.postgres;

import org.upm.inesdata.federated.sql.index.schema.BaseSqlDialectStatements;
import org.eclipse.edc.sql.dialect.PostgresDialect;
import org.eclipse.edc.sql.translation.PostgresqlOperatorTranslator;

/**
 * PostgreSQL-specific SQL dialect statements used for JSON operations. Extends {@link BaseSqlDialectStatements} and
 * provides PostgreSQL-specific implementations for JSON-related operations.
 */
public class PostgresDialectStatements extends BaseSqlDialectStatements {
  /**
   * Constructs a PostgresDialectStatements object using a PostgreSQL operator translator.
   */
  public PostgresDialectStatements() {
    super(new PostgresqlOperatorTranslator());
  }

  /**
   * Retrieves the PostgreSQL operator for formatting as JSON.
   *
   * @return the PostgreSQL JSON cast operator.
   */
  @Override
  public String getFormatAsJsonOperator() {
    return PostgresDialect.getJsonCastOperator();
  }
}
