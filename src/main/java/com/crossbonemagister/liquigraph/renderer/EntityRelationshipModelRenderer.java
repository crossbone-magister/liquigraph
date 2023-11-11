package com.crossbonemagister.liquigraph.renderer;

import com.crossbonemagister.liquigraph.model.Table;

import java.io.IOException;
import java.util.List;

public interface EntityRelationshipModelRenderer {

    void render(List<Table> tables, Appendable output) throws IOException;
}
