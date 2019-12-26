package org.benf.cfr.reader.bytecode.analysis.parse.statement;

import org.benf.cfr.reader.bytecode.analysis.parse.Expression;
import org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriter;
import org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriterFlags;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.*;
import org.benf.cfr.reader.bytecode.analysis.structured.StructuredStatement;
import org.benf.cfr.reader.bytecode.analysis.structured.statement.UnstructuredSwitch;
import org.benf.cfr.reader.util.output.Dumper;

public class SwitchStatement extends AbstractStatement {
    private Expression switchOn;
    private final BlockIdentifier switchBlock;

    public SwitchStatement(Expression switchOn, BlockIdentifier switchBlock) {
        this.switchOn = switchOn;
        this.switchBlock = switchBlock;
    }

    @Override
    public Dumper dump(Dumper dumper) {
        return dumper.print("switch (").dump(switchOn).print(") { // " + switchBlock).newln();
    }

    @Override
    public void replaceSingleUsageLValues(LValueRewriter lValueRewriter, SSAIdentifiers ssaIdentifiers) {
        switchOn = switchOn.replaceSingleUsageLValues(lValueRewriter, ssaIdentifiers, getContainer());
    }

    @Override
    public void rewriteExpressions(ExpressionRewriter expressionRewriter, SSAIdentifiers ssaIdentifiers) {
        switchOn = expressionRewriter.rewriteExpression(switchOn, ssaIdentifiers, getContainer(), ExpressionRewriterFlags.RVALUE);
    }

    @Override
    public void collectLValueUsage(LValueUsageCollector lValueUsageCollector) {
        switchOn.collectUsedLValues(lValueUsageCollector);
    }

    @Override
    public StructuredStatement getStructuredStatement() {
        return new UnstructuredSwitch(switchOn, switchBlock);
    }

    public BlockIdentifier getSwitchBlock() {
        return switchBlock;
    }

    @Override
    public final boolean equivalentUnder(Object o, EquivalenceConstraint constraint) {
        if (o == null) return false;
        if (o == this) return true;
        if (getClass() != o.getClass()) return false;
        SwitchStatement other = (SwitchStatement) o;
        if (!constraint.equivalent(switchOn, other.switchOn)) return false;
        return true;
    }

    @Override
    public boolean fallsToNext() {
        return false;
    }
}
