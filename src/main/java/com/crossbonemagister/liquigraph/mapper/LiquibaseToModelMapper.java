package com.crossbonemagister.liquigraph.mapper;

import com.crossbonemagister.liquigraph.model.Column;
import com.crossbonemagister.liquigraph.model.Table;
import liquibase.change.ColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.change.core.AddColumnChange;
import liquibase.change.core.AddPrimaryKeyChange;
import liquibase.change.core.AddUniqueConstraintChange;
import liquibase.change.core.CreateTableChange;
import liquibase.changelog.DatabaseChangeLog;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LiquibaseToModelMapper {

    Map<String, Table.TableBuilder> tableBuilders = new LinkedHashMap<>();
    Map<String, Column.ColumnBuilder> columnBuilders = new LinkedHashMap<>();

    private static String getColumnKeyName(String tableName, String columnName) {
        return tableName + "_" + columnName;
    }

    public List<Table> map(DatabaseChangeLog changeLog) {
        changeLog.getChangeSets().forEach(changeSet -> {
            changeSet.getChanges().forEach(change -> {
                switch (change) {
                    case CreateTableChange table:
                        createTable(table);
                        break;
                    case AddColumnChange columns:
                        createColumns(columns.getTableName(), columns);
                        break;
                    case AddPrimaryKeyChange primaryKey:
                        addPrimaryKey(primaryKey);
                        break;
                    case AddUniqueConstraintChange uniqueConstraint:
                        addUniqueConstraint(uniqueConstraint.getTableName(), uniqueConstraint);
                        break;
                    default:
                        break;
                }
            });
        });
        columnBuilders.forEach((key, value) -> {
            Table.TableBuilder tableBuilder = tableBuilders.get(key.split("_")[0]);
            tableBuilder.column(value.build());
        });
        return tableBuilders.values().stream().map(Table.TableBuilder::build).toList();
    }

    private void addUniqueConstraint(String tableName, AddUniqueConstraintChange uniqueConstraint) {
        for (String columnName : uniqueConstraint.getColumnNames().split(",")) {
            Column.ColumnBuilder columnBuilder = columnBuilders.get(getColumnKeyName(tableName, columnName));
            columnBuilder.unique(true);
        }
    }

    private void addPrimaryKey(AddPrimaryKeyChange primaryKey) {
        for (String columnName : primaryKey.getColumnNames().split(",")) {
            Column.ColumnBuilder columnBuilder = columnBuilders.get(getColumnKeyName(primaryKey.getTableName(), columnName));
            columnBuilder.primaryKey(true);
        }
    }

    private void createColumns(String tableName, AddColumnChange change) {
        change.getColumns().forEach(column -> {
            createColumn(tableName, column);
        });
    }

    private void createColumn(String tableName, ColumnConfig column) {
        Column.ColumnBuilder columnBuilder = Column.builder();
        columnBuilder.name(column.getName());
        columnBuilder.type(column.getType());
        if (column.getConstraints() != null) {
            ConstraintsConfig constraints = column.getConstraints();
            if (constraints.isPrimaryKey() != null) {
                columnBuilder.primaryKey(constraints.isPrimaryKey());
            }
            if (constraints.isUnique() != null) {
                columnBuilder.unique(constraints.isUnique());
            }
        }
        columnBuilders.put(getColumnKeyName(tableName, column.getName()), columnBuilder);
    }

    private void createTable(CreateTableChange change) {
        Table.TableBuilder tableBuilder = Table.builder();
        tableBuilder.name(change.getTableName());
        tableBuilders.put(change.getTableName(), tableBuilder);
        change.getColumns().forEach(column -> {
            createColumn(change.getTableName(), column);
        });
    }
}
