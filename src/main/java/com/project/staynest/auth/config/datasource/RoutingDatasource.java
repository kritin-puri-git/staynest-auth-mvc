package com.project.staynest.auth.config.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class RoutingDatasource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        String target = readOnly ? "REPLICA" : "MASTER";
        return readOnly ? DataSourceType.REPLICA : DataSourceType.MASTER;
    }
}