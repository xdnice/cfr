package org.benf.cfr.reader.bytecode.analysis.structured.statement;

import org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.BlockIdentifier;
import org.benf.cfr.reader.bytecode.analysis.structured.StructuredStatement;
import org.benf.cfr.reader.entities.exceptions.ExceptionGroup;
import org.benf.cfr.reader.state.TypeUsageCollector;
import org.benf.cfr.reader.util.output.Dumper;

import java.util.Vector;

public class UnstructuredTry extends AbstractUnStructuredStatement {
    private final ExceptionGroup exceptionGroup;

    public UnstructuredTry(ExceptionGroup exceptionGroup) {
        this.exceptionGroup = exceptionGroup;
    }

    @Override
    public Dumper dump(Dumper dumper) {
        return dumper.print("** try " + exceptionGroup + " { ").newln();
    }

    @Override
    public void collectTypeUsages(TypeUsageCollector collector) {
    }

    StructuredStatement getEmptyTry() {
        return new StructuredTry(new Op04StructuredStatement(Block.getEmptyBlock(true)), exceptionGroup.getTryBlockIdentifier());
    }

    @Override
    public StructuredStatement claimBlock(Op04StructuredStatement innerBlock, BlockIdentifier blockIdentifier, Vector<BlockIdentifier> blocksCurrentlyIn) {
        if (blockIdentifier == exceptionGroup.getTryBlockIdentifier()) {
            return new StructuredTry(innerBlock, blockIdentifier);
        } else {
            return null;
        }
    }
}
