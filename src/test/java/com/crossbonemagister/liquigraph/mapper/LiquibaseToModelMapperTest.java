package com.crossbonemagister.liquigraph.mapper;

import com.crossbonemagister.liquigraph.model.Column;
import com.crossbonemagister.liquigraph.model.Table;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.LiquibaseException;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LiquibaseToModelMapperTest {

    @Test
    void mapCreateMap() throws LiquibaseException {
        DatabaseChangeLog databaseChangeLog = loadChangeLog("changelog/create-table.yml");
        List<Table> tables = new LiquibaseToModelMapper().map(databaseChangeLog);
        assertThat(tables).size().isEqualTo(1);
        Table configurationTable = tables.get(0);
        assertThat(configurationTable).extracting(Table::getName).isEqualTo("CONFIGURATION");
        List<Column> columns = configurationTable.getColumns();
        assertThat(columns).size().isEqualTo(2);
        assertThat(columns.getFirst()).extracting(Column::getName, Column::getType, Column::isPrimaryKey, Column::isUnique).containsExactly("KEY_", "VARCHAR(20)", true, false);
        assertThat(columns.getLast()).extracting(Column::getName, Column::getType, Column::isPrimaryKey, Column::isUnique).containsExactly("VALUE_", "VARCHAR(255)", false, false);
    }

    @Test
    void mapAddColumn() throws LiquibaseException {
        DatabaseChangeLog databaseChangeLog = loadChangeLog("changelog/add-column.yml");
        List<Table> tables = new LiquibaseToModelMapper().map(databaseChangeLog);
        assertThat(tables).size().isEqualTo(1);
        Table configurationTable = tables.get(0);
        List<Column> columns = configurationTable.getColumns();
        assertThat(columns).size().isEqualTo(1);
        assertThat(columns.getFirst()).extracting(Column::getName, Column::getType, Column::isPrimaryKey, Column::isUnique).containsExactly("KEY_", "VARCHAR(20)", true, true);
    }

    @Test
    void mapAddPrimaryKey() throws LiquibaseException {
        DatabaseChangeLog databaseChangeLog = loadChangeLog("changelog/add-primary-key.yml");
        List<Table> tables = new LiquibaseToModelMapper().map(databaseChangeLog);
        assertThat(tables).size().isEqualTo(1);
        Table configurationTable = tables.get(0);
        List<Column> columns = configurationTable.getColumns();
        assertThat(columns).size().isEqualTo(1);
        assertThat(columns.getFirst()).extracting(Column::getName, Column::getType, Column::isPrimaryKey, Column::isUnique).containsExactly("KEY_", "VARCHAR(20)", true, false);
    }

    @Test
    void mapAddUniqueConstraint() throws LiquibaseException {
        DatabaseChangeLog databaseChangeLog = loadChangeLog("changelog/add-unique-constraint.yml");
        List<Table> tables = new LiquibaseToModelMapper().map(databaseChangeLog);
        assertThat(tables).size().isEqualTo(1);
        Table configurationTable = tables.get(0);
        List<Column> columns = configurationTable.getColumns();
        assertThat(columns).size().isEqualTo(1);
        assertThat(columns.getFirst()).extracting(Column::getName, Column::getType, Column::isPrimaryKey, Column::isUnique).containsExactly("KEY_", "VARCHAR(20)", false, true);
    }

    @Test
    void mapDropTable() throws LiquibaseException {
        DatabaseChangeLog databaseChangeLog = loadChangeLog("changelog/drop-table.yml");
        List<Table> tables = new LiquibaseToModelMapper().map(databaseChangeLog);
        assertThat(tables).isEmpty();
    }

    @Test
    void mapDropColumnMulti() throws LiquibaseException {
        DatabaseChangeLog databaseChangeLog = loadChangeLog("changelog/drop-column-multi.yml");
        List<Table> tables = new LiquibaseToModelMapper().map(databaseChangeLog);
        assertThat(tables).size().isEqualTo(1);
        Table configurationTable = tables.get(0);
        List<Column> columns = configurationTable.getColumns();
        assertThat(columns).isEmpty();
    }

    @Test
    void mapUnsupportedChangeType() throws LiquibaseException {
        DatabaseChangeLog databaseChangeLog = loadChangeLog("changelog/unsupported-change-type.yml");
        List<Table> tables = new LiquibaseToModelMapper().map(databaseChangeLog);
        assertThat(tables).isEmpty();
    }

    private static DatabaseChangeLog loadChangeLog(String changeLogFile) throws LiquibaseException {
        ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
        ChangeLogParser parser = ChangeLogParserFactory.getInstance().getParser(changeLogFile, resourceAccessor);
        return parser.parse(changeLogFile, new ChangeLogParameters(), resourceAccessor);
    }
}
