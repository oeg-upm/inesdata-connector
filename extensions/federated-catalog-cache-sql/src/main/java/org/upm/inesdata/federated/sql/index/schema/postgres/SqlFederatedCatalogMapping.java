package org.upm.inesdata.federated.sql.index.schema.postgres;

import org.eclipse.edc.sql.translation.JsonFieldTranslator;
import org.eclipse.edc.sql.translation.TranslationMapping;
import org.upm.inesdata.federated.sql.index.schema.SqlFederatedCatalogStatements;

/**
 * Maps fields of a federated catalog  onto the corresponding SQL schema (= column names) enabling access through
 * Postgres JSON operators where applicable
 */
public class SqlFederatedCatalogMapping extends TranslationMapping {
  /**
   * Constructs a mapping for SQL federated catalog columns using the provided SQL federated catalog statements. This
   * mapping specifies how federated catalog fields correspond to database columns.
   *
   * @param statements the SQL statements specific to the federated catalog schema.
   */
  public SqlFederatedCatalogMapping(SqlFederatedCatalogStatements statements) {
    add("id", statements.getCatalogIdColumn());
    add("offers", new JsonFieldTranslator(statements.getDatasetOffersColumn()));
    add("properties", new JsonFieldTranslator(statements.getCatalogPropertiesColumn()));
  }

}
