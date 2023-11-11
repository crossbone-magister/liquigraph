package com.crossbonemagister.liquigraph;

import com.crossbonemagister.liquigraph.loader.LiquibaseChangeLogLoader;
import com.crossbonemagister.liquigraph.mapper.LiquibaseToModelMapper;
import com.crossbonemagister.liquigraph.model.Table;
import com.crossbonemagister.liquigraph.renderer.impl.EntityRelationshipModelMermaidRenderer;
import liquibase.changelog.DatabaseChangeLog;

import java.util.List;

public class LiquigraphCliApplication {

    public static void main(String[] args) throws Exception {
        if (args.length <= 0) {
            throw new IllegalArgumentException("Missing argument: change log file");
        }
        String changeLogFile = args[0];
        if (changeLogFile == null || changeLogFile.isEmpty()) {
            throw new IllegalArgumentException("Invalid argument: change log file");
        }
        LiquibaseChangeLogLoader liquibaseChangeLogLoader = new LiquibaseChangeLogLoader();
        DatabaseChangeLog changeLog = liquibaseChangeLogLoader.load(changeLogFile);
        LiquibaseToModelMapper modelMapper = new LiquibaseToModelMapper();
        List<Table> model = modelMapper.map(changeLog);
        EntityRelationshipModelMermaidRenderer renderer = new EntityRelationshipModelMermaidRenderer();
        renderer.render(model, System.out);
    }

}
