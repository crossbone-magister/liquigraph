package com.crossbonemagister.liquigraph.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class Table {
    private String name;
    @Singular("column")
    private List<Column> columns;
}
