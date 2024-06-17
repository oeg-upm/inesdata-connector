package org.upm.inesdata.federated.sql.index.schema;

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.translation.SqlOperatorTranslator;
import org.eclipse.edc.sql.translation.SqlQueryStatement;
import org.upm.inesdata.federated.sql.index.schema.postgres.SqlDatasetMapping;
import org.upm.inesdata.federated.sql.index.schema.postgres.SqlFederatedCatalogMapping;

import static java.lang.String.format;

public class BaseSqlDialectStatements implements SqlFederatedCatalogStatements {

    protected final SqlOperatorTranslator operatorTranslator;

    public BaseSqlDialectStatements(SqlOperatorTranslator operatorTranslator) {
        this.operatorTranslator = operatorTranslator;
    }

    @Override
    public String getInsertCatalogTemplate() {
        return executeStatement()
            .column(getCatalogIdColumn())
            .column(getCatalogParticipantIdColumn())
            .jsonColumn(getCatalogPropertiesColumn())
            .column(getCatalogExpiredColumn())
            .insertInto(getCatalogTable());
    }

    @Override
    public String getSelectCatalogTemplate() {
        return format("SELECT * FROM %s AS a", getCatalogTable());
    }

    @Override
    public String getDeleteCatalogByParticipantIdTemplate() {
        return executeStatement()
            .delete(getCatalogTable(), getCatalogParticipantIdColumn());
    }

    @Override
    public String getInsertDataServiceTemplate() {
        return executeStatement()
            .column(getDataServiceIdColumn())
            .column(getDataServiceTermsColumn())
            .column(getDataServiceEndpointUrlColumn())
            .insertInto(getDataServiceTable());
    }

    @Override
    public String getInsertCatalogDataServiceTemplate() {
        return executeStatement()
            .column("catalog_id")
            .column("data_service_id")
            .insertInto(getCatalogDataServiceTable());
    }

    // Dataset CRUD methods
    @Override
    public String getInsertDatasetTemplate() {
        return executeStatement()
            .column(getDatasetIdColumn())
            .jsonColumn(getDatasetOffersColumn())
            .jsonColumn(getDatasetPropertiesColumn())
            .column(getDatasetCatalogIdColumn())
            .insertInto(getDatasetTable());
    }

    @Override
    public String getInsertDistributionTemplate() {
        return executeStatement()
            .column(getDistributionFormatColumn())
            .column(getDistributionDataServiceIdColumn())
            .column(getDistributionDatasetIdColumn())
            .insertInto(getDistributionTable());
    }

    @Override
    public SqlQueryStatement createQuery(QuerySpec querySpec) {
        return new SqlQueryStatement(getSelectCatalogTemplate(), querySpec, new SqlFederatedCatalogMapping(this), operatorTranslator);
    }

    @Override
    public SqlQueryStatement createDatasetQuery(QuerySpec querySpec) {
        return new SqlQueryStatement(getSelectDatasetTemplate(), querySpec, new SqlDatasetMapping(this), operatorTranslator);
    }

    @Override
    public String getDeleteExpiredCatalogsTemplate() {
        return executeStatement()
            .delete(getCatalogTable(), getCatalogExpiredColumn());
    }

    @Override
    public String getExpireAllCatalogsTemplate() {
        return executeStatement()
            .column(getCatalogExpiredColumn())
            .update(getCatalogTable(), getCatalogExpiredColumn());
    }

    @Override
    public String getSelectDatasetsForCatalogTemplate() {
        return format("SELECT * FROM %s AS a WHERE catalog_id = ?", getDatasetTable());
    }

    @Override
    public String getSelectDistributionsForDatasetTemplate() {
        return format("SELECT * FROM %s AS a WHERE dataset_id = ?", getDistributionTable());
    }
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

    @Override
    public String getSelectDataServicesForIdTemplate() {
        return format("SELECT * FROM %s AS a WHERE id = ?", getDataServiceTable());
    }


    @Override
    public String getDeleteDistributionsForCatalogTemplate() {
        return format("DELETE FROM %s WHERE dataset_id IN (SELECT id FROM %s WHERE catalog_id = ?)", getDistributionTable(), getDatasetTable());
    }

    @Override
    public String getDeleteCatalogDataServicesTemplate() {

        return format("DELETE FROM %s WHERE catalog_id = ?",getCatalogDataServiceTable());
    }

    @Override
    public String getDeleteOrphanDataServicesTemplate() {
        return format("DELETE FROM %s WHERE id NOT IN (SELECT data_service_id FROM %s)" +
            " AND id NOT IN (SELECT data_service_id FROM %s)",getDataServiceTable(), getCatalogDataServiceTable(), getDistributionTable());
    }

    @Override
    public String getDeleteDatasetsForCatalogTemplate() {
        return format("DELETE FROM %s WHERE catalog_id = ?",getDatasetTable());
    }


    @Override
    public String getSelectCatalogForParticipantIdTemplate() {
        return format("SELECT * FROM %s AS a WHERE participant_id = ?", getCatalogTable());
    }



    @Override
    public String getSelectDatasetTemplate() {
        return format("SELECT * FROM %s AS a", getDatasetTable());
    }
}
