package com.crossbonemagister.liquigraph.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Column {
    String name;
    String type;
    boolean primaryKey;
    boolean unique;
}
