package com.crossbonemagister.liquigraph.mapper;

import com.crossbonemagister.liquigraph.model.Column;
import com.crossbonemagister.liquigraph.model.Table;
import liquibase.change.ColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.change.core.*;
import liquibase.changelog.DatabaseChangeLog;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LiquibaseToModelMapper {

    public static final int TABLE_MULTI_KEY_INDEX = 0;
    public static final int COLUMN_MULTI_KEY_INDEX = 1;
    Map<String, Table.TableBuilder> tableBuilders = new LinkedHashMap<>();
    MultiKeyMap<String, Column.ColumnBuilder> columnBuilders = MultiKeyMap.multiKeyMap(new LinkedMap<>());

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
                    case DropTableChange dropTable:
                        dropTable(dropTable);
                        break;
                    case DropColumnChange dropColumn:
                        dropColumn(dropColumn);
                        break;
                    case RenameColumnChange renameColumn:
                        renameColumn(renameColumn);
                        break;
                    case RenameTableChange renameTable:
                        renameTable(renameTable);
                        break;
                    default:
                        break;
                }
            });
        });
        columnBuilders.forEach((key, value) -> {
            Table.TableBuilder tableBuilder = tableBuilders.get(key.getKey(TABLE_MULTI_KEY_INDEX));
            tableBuilder.column(value.build());
        });
        return tableBuilders.values().stream().map(Table.TableBuilder::build).toList();
    }

    private void renameTable(RenameTableChange renameTable) {
        renameTable(renameTable.getOldTableName(), renameTable.getNewTableName());
    }

    private void renameTable(String oldName, String newName) {
        Table.TableBuilder tableBuilder = tableBuilders.get(oldName);
        tableBuilder.name(newName);
        //TODO: This renames the table but doesn't maintain insertion order. Is it a problem or not?
        tableBuilders.remove(oldName);
        tableBuilders.put(newName, tableBuilder);
        columnBuilders.keySet().stream().filter(key -> StringUtils.equals(key.getKey(TABLE_MULTI_KEY_INDEX), oldName)).toList()
            .forEach(key -> {
                columnBuilders.put(newName, key.getKey(COLUMN_MULTI_KEY_INDEX), columnBuilders.get(key));
                columnBuilders.remove(key);
            });
    }

    private void renameColumn(RenameColumnChange renameColumn) {
        renameColumn(renameColumn.getTableName(), renameColumn.getOldColumnName(), renameColumn.getNewColumnName());
    }

    private void renameColumn(String table, String oldName, String newName) {
        Column.ColumnBuilder columnBuilder = columnBuilders.get(table, oldName);
        columnBuilder.name(newName);
        //TODO: This renames the column but doesn't maintain insertion order. Is it a problem or not?
        columnBuilders.removeMultiKey(table, oldName);
        columnBuilders.put(table, newName, columnBuilder);
    }

    private void dropColumn(DropColumnChange dropColumn) {
        if (StringUtils.isNotBlank(dropColumn.getColumnName())) {
            dropColumn(dropColumn.getTableName(), dropColumn.getColumnName());
        } else {
            dropColumn.getColumns().forEach(column -> {
                dropColumn(dropColumn.getTableName(), column.getName());
            });
        }
    }

    private void dropColumn(String table, String column) {
        columnBuilders.removeMultiKey(table, column);
    }

    private void dropTable(DropTableChange dropTable) {
        Set<MultiKey<? extends String>> allTableColumns = columnBuilders.keySet().stream()
            .filter(key -> StringUtils.equals(key.getKey(TABLE_MULTI_KEY_INDEX), dropTable.getTableName()))
            .collect(Collectors.toSet());
        allTableColumns.forEach(
            key -> dropColumn(key.getKey(TABLE_MULTI_KEY_INDEX), key.getKey(COLUMN_MULTI_KEY_INDEX))
        );
        tableBuilders.remove(dropTable.getTableName());
    }

    private void addUniqueConstraint(String tableName, AddUniqueConstraintChange uniqueConstraint) {
        for (String columnName : uniqueConstraint.getColumnNames().split(",")) {
            Column.ColumnBuilder columnBuilder = columnBuilders.get(tableName, columnName);
            columnBuilder.unique(true);
        }
    }

    private void addPrimaryKey(AddPrimaryKeyChange primaryKey) {
        for (String columnName : primaryKey.getColumnNames().split(",")) {
            Column.ColumnBuilder columnBuilder = columnBuilders.get(primaryKey.getTableName(), columnName);
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
        columnBuilders.put(tableName, column.getName(), columnBuilder);
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
