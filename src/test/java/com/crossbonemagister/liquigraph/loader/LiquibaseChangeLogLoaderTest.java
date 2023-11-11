package com.crossbonemagister.liquigraph.loader;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class LiquibaseChangeLogLoaderTest {

    @Test
    void load() throws FileNotFoundException, LiquibaseException {
        LiquibaseChangeLogLoader loader = new LiquibaseChangeLogLoader();
        DatabaseChangeLog databaseChangeLog = loader.load("changelog/create-table.yml");
        assertThat(databaseChangeLog).isNotNull();
        assertThat(databaseChangeLog.getChangeSets()).size().isEqualTo(1);
    }
}
