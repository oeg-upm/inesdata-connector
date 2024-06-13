package org.upm.inesdata.federated.sql.index.schema;

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.translation.SqlOperatorTranslator;
import org.eclipse.edc.sql.translation.SqlQueryStatement;
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
    public String getUpdateCatalogTemplate() {
        return executeStatement()
            .column(getCatalogParticipantIdColumn())
            .jsonColumn(getCatalogPropertiesColumn())
            .column(getCatalogExpiredColumn())
            .update(getCatalogTable(), getCatalogIdColumn());
    }

    @Override
    public String getCountCatalogByIdClause() {
        return format("SELECT COUNT(*) AS %s FROM %s WHERE %s = ?",
            getCountVariableName(),
            getCatalogTable(),
            getCatalogIdColumn());
    }

    @Override
    public String getSelectCatalogTemplate() {
        return format("SELECT * FROM %s AS a", getCatalogTable());
    }

    @Override
    public String getDeleteCatalogByIdTemplate() {
        return executeStatement()
            .delete(getCatalogTable(), getCatalogIdColumn());
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
    public String getUpdateDataServiceTemplate() {
        return executeStatement()
            .column(getDataServiceTermsColumn())
            .column(getDataServiceEndpointUrlColumn())
            .update(getDataServiceTable(), getDataServiceIdColumn());
    }

    @Override
    public String getCountDataServiceByIdClause() {
        return format("SELECT COUNT(*) AS %s FROM %s WHERE %s = ?",
            getCountVariableName(),
            getDataServiceTable(),
            getDataServiceIdColumn());
    }

    @Override
    public String getSelectDataServiceTemplate() {
        return format("SELECT * FROM %s AS a", getDataServiceTable());
    }

    @Override
    public String getDeleteDataServiceByIdTemplate() {
        return executeStatement()
            .delete(getDataServiceTable(), getDataServiceIdColumn());
    }

    @Override
    public String getInsertCatalogDataServiceTemplate() {
        return executeStatement()
            .column("catalog_id")
            .column("dataset_id")
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
    public String getUpdateDatasetTemplate() {
        return executeStatement()
            .jsonColumn(getDatasetOffersColumn())
            .jsonColumn(getDatasetPropertiesColumn())
            .column(getDatasetCatalogIdColumn())
            .update(getDatasetTable(), getDatasetIdColumn());
    }

    @Override
    public String getCountDatasetByIdClause() {
        return format("SELECT COUNT(*) AS %s FROM %s WHERE %s = ?",
            getCountVariableName(),
            getDatasetTable(),
            getDatasetIdColumn());
    }

    @Override
    public String getSelectDatasetTemplate() {
        return format("SELECT * FROM %s AS a", getDatasetTable());
    }

    @Override
    public String getDeleteDatasetByIdTemplate() {
        return executeStatement()
            .delete(getDatasetTable(), getDatasetIdColumn());
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
    public String getUpdateDistributionTemplate() {
        return executeStatement()
            .column(getDistributionFormatColumn())
            .column(getDistributionDataServiceIdColumn())
            .update(getDistributionTable(), getDistributionDatasetIdColumn());
    }

    @Override
    public String getCountDistributionByIdClause() {
        return format("SELECT COUNT(*) AS %s FROM %s WHERE %s = ?",
            getCountVariableName(),
            getDistributionTable(),
            getDistributionDatasetIdColumn());
    }

    @Override
    public String getSelectDistributionTemplate() {
        return format("SELECT * FROM %s AS a", getDistributionTable());
    }

    @Override
    public String getDeleteDistributionByIdTemplate() {
        return executeStatement()
            .delete(getDistributionTable(), getDistributionDatasetIdColumn());
    }

    @Override
    public String getInsertDatasetDistributionTemplate() {
        return executeStatement()
            .column("dataset_id")
            .column("distribution_id")
            .insertInto("edc_dataset_distribution");
    }
    @Override
    public String getCountVariableName() {
        return "COUNT";
    }

    @Override
    public SqlQueryStatement createQuery(QuerySpec querySpec) {
        return new SqlQueryStatement(getSelectCatalogTemplate(), querySpec, new SqlFederatedCatalogMapping(this), operatorTranslator);

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
        return format("SELECT * FROM %s AS a WHERE catalog_id = ?v", getDatasetTable());
    }

    @Override
    public String getSelectDistributionsForDatasetTemplate() {
        return format("SELECT * FROM %s AS a WHERE dataset_id = ?v", getDistributionTable());
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
        return format("SELECT * FROM %s AS a WHERE id = ?v", getDataServiceTable());
    }
}
