package org.upm.inesdata.federated.sql.index.schema;

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.translation.SqlOperatorTranslator;
import org.eclipse.edc.sql.translation.SqlQueryStatement;
import org.upm.inesdata.federated.sql.index.schema.postgres.SqlDatasetMapping;
import org.upm.inesdata.federated.sql.index.schema.postgres.SqlFederatedCatalogMapping;
import org.upm.inesdata.search.extension.InesdataSqlQueryStatement;

import static java.lang.String.format;

/**
 * Base implementation of {@link SqlFederatedCatalogStatements} providing SQL statements
 * for managing federated catalog entities in a SQL database.
 */
public class BaseSqlDialectStatements implements SqlFederatedCatalogStatements {

    protected final SqlOperatorTranslator operatorTranslator;

    /**
     * Constructs a {@code BaseSqlDialectStatements} instance with the specified SQL operator translator.
     *
     * @param operatorTranslator the SQL operator translator used for translating SQL operators.
     */
    public BaseSqlDialectStatements(SqlOperatorTranslator operatorTranslator) {
        this.operatorTranslator = operatorTranslator;
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getInsertCatalogTemplate()
     */
    @Override
    public String getInsertCatalogTemplate() {
        return executeStatement()
            .column(getCatalogIdColumn())
            .column(getCatalogParticipantIdColumn())
            .jsonColumn(getCatalogPropertiesColumn())
            .column(getCatalogExpiredColumn())
            .insertInto(getCatalogTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getSelectCatalogTemplate()
     */
    @Override
    public String getSelectCatalogTemplate() {
        return format("SELECT * FROM %s AS a", getCatalogTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getDeleteCatalogByParticipantIdTemplate()
     */
    @Override
    public String getDeleteCatalogByParticipantIdTemplate() {
        return executeStatement()
            .delete(getCatalogTable(), getCatalogParticipantIdColumn());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getInsertDataServiceTemplate()
     */
    @Override
    public String getInsertDataServiceTemplate() {
        return executeStatement()
            .column(getDataServiceIdColumn())
            .column(getDataServiceTermsColumn())
            .column(getDataServiceEndpointUrlColumn())
            .insertInto(getDataServiceTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getInsertCatalogDataServiceTemplate()
     */
    @Override
    public String getInsertCatalogDataServiceTemplate() {
        return executeStatement()
            .column("catalog_id")
            .column("data_service_id")
            .insertInto(getCatalogDataServiceTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getInsertDatasetTemplate()
     */
    @Override
    public String getInsertDatasetTemplate() {
        return executeStatement()
            .column(getDatasetIdColumn())
            .jsonColumn(getDatasetOffersColumn())
            .jsonColumn(getDatasetPropertiesColumn())
            .column(getDatasetCatalogIdColumn())
            .insertInto(getDatasetTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getInsertDistributionTemplate()
     */
    @Override
    public String getInsertDistributionTemplate() {
        return executeStatement()
            .column(getDistributionFormatColumn())
            .column(getDistributionDataServiceIdColumn())
            .column(getDistributionDatasetIdColumn())
            .insertInto(getDistributionTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#createQuery(QuerySpec)
     */
    @Override
    public SqlQueryStatement createQuery(QuerySpec querySpec) {
        return new SqlQueryStatement(getSelectCatalogTemplate(), querySpec, new SqlFederatedCatalogMapping(this), operatorTranslator);
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#createDatasetQuery(QuerySpec)
     */
    @Override
    public InesdataSqlQueryStatement createDatasetQuery(QuerySpec querySpec) {
        return new InesdataSqlQueryStatement(getSelectDatasetTemplate(), querySpec, new SqlDatasetMapping(this), operatorTranslator);
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getDeleteExpiredCatalogsTemplate()
     */
    @Override
    public String getDeleteExpiredCatalogsTemplate() {
        return executeStatement()
            .delete(getCatalogTable(), getCatalogExpiredColumn());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getExpireAllCatalogsTemplate()
     */
    @Override
    public String getExpireAllCatalogsTemplate() {
        return executeStatement()
            .column(getCatalogExpiredColumn())
            .update(getCatalogTable(), getCatalogExpiredColumn());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getSelectDatasetsForCatalogTemplate()
     */
    @Override
    public String getSelectDatasetsForCatalogTemplate() {
        return format("SELECT * FROM %s AS a WHERE catalog_id = ?", getDatasetTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getSelectDistributionsForDatasetTemplate()
     */
    @Override
    public String getSelectDistributionsForDatasetTemplate() {
        return format("SELECT * FROM %s AS a WHERE dataset_id = ?", getDistributionTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getSelectDataServicesForCatalogTemplate()
     */
    @Override
    public String getSelectDataServicesForCatalogTemplate() {
        return format(
            "SELECT ds.* FROM %s AS ds " +
                "JOIN %s AS cds ON ds.id = cds.data_service_id " +
                "WHERE cds.catalog_id = ?",
            getDataServiceTable(),
            getCatalogDataServiceTable()
        );
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getSelectDataServicesForIdTemplate()
     */
    @Override
    public String getSelectDataServicesForIdTemplate() {
        return format("SELECT * FROM %s AS a WHERE id = ?", getDataServiceTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getDeleteDistributionsForCatalogTemplate()
     */
    @Override
    public String getDeleteDistributionsForCatalogTemplate() {
        return format("DELETE FROM %s WHERE dataset_id IN (SELECT id FROM %s WHERE catalog_id = ?)", getDistributionTable(), getDatasetTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getDeleteCatalogDataServicesTemplate()
     */
    @Override
    public String getDeleteCatalogDataServicesTemplate() {
        return format("DELETE FROM %s WHERE catalog_id = ?", getCatalogDataServiceTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getDeleteOrphanDataServicesTemplate()
     */
    @Override
    public String getDeleteOrphanDataServicesTemplate() {
        return format("DELETE FROM %s WHERE id NOT IN (SELECT data_service_id FROM %s)" +
            " AND id NOT IN (SELECT data_service_id FROM %s)", getDataServiceTable(), getCatalogDataServiceTable(), getDistributionTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getDeleteDatasetsForCatalogTemplate()
     */
    @Override
    public String getDeleteDatasetsForCatalogTemplate() {
        return format("DELETE FROM %s WHERE catalog_id = ?", getDatasetTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getSelectCatalogForParticipantIdTemplate()
     */
    @Override
    public String getSelectCatalogForParticipantIdTemplate() {
        return format("SELECT * FROM %s AS a WHERE participant_id = ?", getCatalogTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getSelectDatasetTemplate()
     */
    @Override
    public String getSelectDatasetTemplate() {
        return format("SELECT * FROM %s AS a", getDatasetTable());
    }

    /**
     * {@inheritDoc}
     *
     * @see SqlFederatedCatalogStatements#getSelectExpiredCatalogsTemplate()
     */
    @Override
    public String getSelectExpiredCatalogsTemplate() {
        return format("SELECT * FROM %s AS a WHERE expired = true", getCatalogTable());
    }
}
