package com.crossbonemagister.liquigraph;

import com.crossbonemagister.liquigraph.mapper.LiquibaseToModelMapper;
import com.crossbonemagister.liquigraph.model.Table;
import com.crossbonemagister.liquigraph.render.EntityRelationshipModelMermaidRenderer;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.resource.SearchPathResourceAccessor;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        String changeLogFile = "src/main/resources/changelog/changelog.yml";
        ResourceAccessor resourceAccessor = createResourceAccessor();
        ChangeLogParser parser = ChangeLogParserFactory.getInstance().getParser(changeLogFile, resourceAccessor);
        DatabaseChangeLog changeLog = parser.parse(changeLogFile, new ChangeLogParameters(), resourceAccessor);
        LiquibaseToModelMapper modelMapper = new LiquibaseToModelMapper();
        List<Table> model = modelMapper.map(changeLog);
        EntityRelationshipModelMermaidRenderer renderer = new EntityRelationshipModelMermaidRenderer();
        renderer.render(model, System.out);
    }

    private static ResourceAccessor createResourceAccessor() throws FileNotFoundException {
        Path searchPath = Paths.get(".").toAbsolutePath();
        return new SearchPathResourceAccessor(new DirectoryResourceAccessor(searchPath), new ClassLoaderResourceAccessor());
    }
}
