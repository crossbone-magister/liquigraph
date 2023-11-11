package com.crossbonemagister.liquigraph.render.impl;

import com.crossbonemagister.liquigraph.model.Column;
import com.crossbonemagister.liquigraph.model.Table;
import com.crossbonemagister.liquigraph.render.EntityRelationshipModelRenderer;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

public class EntityRelationshipModelMermaidRenderer implements EntityRelationshipModelRenderer {
    @Override
    public void render(List<Table> tables, Appendable output) throws IOException {
        StringBuilder rendition = new StringBuilder();
        rendition.append("erDiagram");
        rendition.append(System.lineSeparator());
        for (Table table : tables) {
            renderTable(table, rendition);
            rendition.append(System.lineSeparator());
        }
        output.append(rendition.toString());
    }

    private void renderTable(Table table, StringBuilder rendition) throws IOException {
        rendition.append(table.getName()).append(" {");
        rendition.append(System.lineSeparator());
        for (Column column : table.getColumns()) {
            renderColumn(column, rendition);
            rendition.append(System.lineSeparator());
        }
        rendition.append("}");
    }

    private void renderColumn(Column column, StringBuilder rendition) {
        rendition
            .append(column.getType())
            .append(" ")
            .append(column.getName());
        StringJoiner constraintsRendition = new StringJoiner(",", " ", "");
        constraintsRendition.setEmptyValue("");
        if (column.isPrimaryKey()) {
            constraintsRendition.add("PK");
        }
        if (column.isUnique()) {
            constraintsRendition.add("UK");
        }
        rendition.append(constraintsRendition);
    }
}
