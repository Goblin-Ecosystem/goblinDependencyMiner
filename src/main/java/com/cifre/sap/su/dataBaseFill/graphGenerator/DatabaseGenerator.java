package com.cifre.sap.su.dataBaseFill.graphGenerator;

import com.cifre.sap.su.dataBaseFill.graphInstruction.GraphInstruction;

import java.util.function.Consumer;

public abstract class DatabaseGenerator implements Consumer<GraphInstruction> {
    public abstract void accept(GraphInstruction graphInstruction);
    @Override
    public Consumer<GraphInstruction> andThen(Consumer<? super GraphInstruction> after) {
        return Consumer.super.andThen(after);
    }
}
