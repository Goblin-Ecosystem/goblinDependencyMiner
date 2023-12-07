package com.cifre.sap.su.dataBaseFill.graphInstruction;

import com.cifre.sap.su.model.Release;

import java.util.Set;

public class CreateVertexInstruction implements GraphInstruction {
    private final Set<Release> vertices;

    public CreateVertexInstruction(Set<Release> vertices) {
        this.vertices = vertices;
    }

    @Override
    public Object getObject() {
        return vertices;
    }
}
