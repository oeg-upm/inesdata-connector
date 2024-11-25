/*
 *  Copyright (c) 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.upm.inesdata.search.extension;

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.translation.SqlOperatorTranslator;
import org.eclipse.edc.sql.translation.SqlQueryStatement;
import org.eclipse.edc.sql.translation.TranslationMapping;

/**
 * Maps a {@link QuerySpec} to a single SQL {@code SELECT ... FROM ... WHERE ...} statement. The {@code SELECT ...} part
 * is passed in through the constructor, and the rest of the query is assembled dynamically, based on the
 * {@link QuerySpec} and the {@link TranslationMapping}.
 */
public class InesdataSqlQueryStatement extends SqlQueryStatement{

    /**
     * Initializes this SQL Query Statement.
     *
     * @param selectStatement    The SELECT clause, e.g. {@code SELECT * FROM your_table}
     * @param query              a {@link QuerySpec} that contains a query in the canonical format
     * @param rootModel          A {@link TranslationMapping} that enables mapping from canonical to the SQL-specific
     *                           model/format
     * @param operatorTranslator the {@link SqlOperatorTranslator} instance.
     */
    public InesdataSqlQueryStatement(String selectStatement, QuerySpec query, TranslationMapping rootModel, SqlOperatorTranslator operatorTranslator) {
        super(selectStatement, query, rootModel, new CriterionToWhereClauseConverterImpl(rootModel, operatorTranslator));
    }
}
