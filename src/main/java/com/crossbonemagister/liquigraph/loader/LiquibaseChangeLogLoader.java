package com.crossbonemagister.liquigraph.loader;

import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.LiquibaseException;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.resource.SearchPathResourceAccessor;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LiquibaseChangeLogLoader {

    public DatabaseChangeLog load(String changeLogFileLocation) throws FileNotFoundException, LiquibaseException {
        ResourceAccessor resourceAccessor = createResourceAccessor();
        ChangeLogParser parser = ChangeLogParserFactory.getInstance().getParser(changeLogFileLocation, resourceAccessor);
        return parser.parse(changeLogFileLocation, new ChangeLogParameters(), resourceAccessor);
    }

    private static ResourceAccessor createResourceAccessor() throws FileNotFoundException {
        Path searchPath = Paths.get(".").toAbsolutePath();
        return new SearchPathResourceAccessor(new DirectoryResourceAccessor(searchPath), new ClassLoaderResourceAccessor());
    }
}
