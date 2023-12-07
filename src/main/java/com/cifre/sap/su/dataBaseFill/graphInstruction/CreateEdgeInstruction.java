package com.cifre.sap.su.dataBaseFill.graphInstruction;

import com.cifre.sap.su.model.Link;

import java.util.List;

public class CreateEdgeInstruction implements GraphInstruction {
    private final List<Link> edges;

    public CreateEdgeInstruction(List<Link> edges) {
        this.edges = edges;
    }

    @Override
    public Object getObject() {
        return edges;
    }
}
