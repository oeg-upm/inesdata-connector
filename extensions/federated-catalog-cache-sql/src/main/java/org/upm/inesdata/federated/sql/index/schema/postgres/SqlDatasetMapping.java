package org.upm.inesdata.federated.sql.index.schema.postgres;

import org.eclipse.edc.sql.translation.JsonFieldTranslator;
import org.eclipse.edc.sql.translation.TranslationMapping;
import org.upm.inesdata.federated.sql.index.schema.SqlFederatedCatalogStatements;

/**
 * Maps fields of a federated catalog  onto the
 * corresponding SQL schema (= column names) enabling access through Postgres JSON operators where applicable
 */
public class SqlDatasetMapping extends TranslationMapping {

    public SqlDatasetMapping(SqlFederatedCatalogStatements statements) {
        add("id", statements.getDatasetIdColumn());
        add("offers", new JsonFieldTranslator(statements.getDatasetOffersColumn()));
        add("properties", new JsonFieldTranslator(statements.getDatasetPropertiesColumn()));
        add("catalog_id", statements.getDatasetCatalogIdColumn());
    }

}
