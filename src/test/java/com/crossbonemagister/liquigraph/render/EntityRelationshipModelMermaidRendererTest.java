package com.crossbonemagister.liquigraph.render;

import com.crossbonemagister.liquigraph.model.Column;
import com.crossbonemagister.liquigraph.model.Table;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class EntityRelationshipModelMermaidRendererTest {

    @Test
    void render() throws IOException {
        StringBuilder output = new StringBuilder();
        Table table = Table.builder()
            .name("TEST")
            .column(Column.builder().name("PRIMARY_KEY").type("INTEGER").primaryKey(true).build())
            .column(Column.builder().name("UNIQUE_KEY").type("VARCHAR").unique(true).build())
            .column(Column.builder().name("PRIMARY_UNIQUE_KEY").type("VARCHAR").primaryKey(true).unique(true).build())
            .build();
        new EntityRelationshipModelMermaidRenderer().render(List.of(table), output);
        Assertions.assertThat(output.toString()).isEqualTo(
            "erDiagram" + System.lineSeparator() +
                "TEST {" + System.lineSeparator() +
                "INTEGER PRIMARY_KEY PK" + System.lineSeparator() +
                "VARCHAR UNIQUE_KEY UK" + System.lineSeparator() +
                "VARCHAR PRIMARY_UNIQUE_KEY PK,UK" + System.lineSeparator() +
                "}" + System.lineSeparator()
        );
    }
}
