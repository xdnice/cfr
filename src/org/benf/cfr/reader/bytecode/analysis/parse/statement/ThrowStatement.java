package org.benf.cfr.reader.bytecode.analysis.parse.statement;

import org.benf.cfr.reader.bytecode.analysis.parse.Expression;
import org.benf.cfr.reader.bytecode.analysis.parse.rewriters.CloneHelper;
import org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriter;
import org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriterFlags;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.EquivalenceConstraint;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.LValueRewriter;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.LValueUsageCollector;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.SSAIdentifiers;
import org.benf.cfr.reader.bytecode.analysis.structured.StructuredStatement;
import org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredThrow;
import org.benf.cfr.reader.entities.exceptions.ExceptionCheck;
import org.benf.cfr.reader.util.output.Dumper;

public class ThrowStatement extends ReturnStatement {
    private Expression rvalue;

    public ThrowStatement(Expression rvalue) {
        this.rvalue = rvalue;
    }

    @Override
    public ReturnStatement deepClone(CloneHelper cloneHelper) {
        return new ThrowStatement(cloneHelper.replaceOrClone(rvalue));
    }

    @Override
    public Dumper dump(Dumper dumper) {
        return dumper.keyword("throw ").dump(rvalue).endCodeln();
    }

    @Override
    public void replaceSingleUsageLValues(LValueRewriter lValueRewriter, SSAIdentifiers ssaIdentifiers) {
        this.rvalue = rvalue.replaceSingleUsageLValues(lValueRewriter, ssaIdentifiers, getContainer());
    }

    @Override
    public void rewriteExpressions(ExpressionRewriter expressionRewriter, SSAIdentifiers ssaIdentifiers) {
        rvalue = expressionRewriter.rewriteExpression(rvalue, ssaIdentifiers, getContainer(), ExpressionRewriterFlags.RVALUE);
    }

    @Override
    public void collectLValueUsage(LValueUsageCollector lValueUsageCollector) {
        rvalue.collectUsedLValues(lValueUsageCollector);
    }

    @Override
    public StructuredStatement getStructuredStatement() {
        return new StructuredThrow(rvalue);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ThrowStatement)) return false;
        ThrowStatement other = (ThrowStatement) o;
        return rvalue.equals(other.rvalue);
    }

    // HAHAHAHAH
    @Override
    public boolean canThrow(ExceptionCheck caught) {
        // Oh good grief.  this is going to be a massive pain.  What could this type be throwing?
        // For now, let's handle the simple case where it's throwing a new exception.
        return caught.checkAgainstException(rvalue);
    }

    @Override
    public final boolean equivalentUnder(Object o, EquivalenceConstraint constraint) {
        if (o == null) return false;
        if (o == this) return true;
        if (getClass() != o.getClass()) return false;
        ThrowStatement other = (ThrowStatement) o;
        if (!constraint.equivalent(rvalue, other.rvalue)) return false;
        return true;
    }

}
