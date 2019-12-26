package org.benf.cfr.reader.bytecode.analysis.parse.expression;

import org.benf.cfr.reader.bytecode.analysis.parse.Expression;
import org.benf.cfr.reader.bytecode.analysis.parse.LValue;
import org.benf.cfr.reader.bytecode.analysis.parse.StatementContainer;
import org.benf.cfr.reader.bytecode.analysis.parse.expression.misc.Precedence;
import org.benf.cfr.reader.bytecode.analysis.parse.lvalue.StackSSALabel;
import org.benf.cfr.reader.bytecode.analysis.parse.rewriters.CloneHelper;
import org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriter;
import org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriterFlags;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.*;
import org.benf.cfr.reader.state.TypeUsageCollector;
import org.benf.cfr.reader.util.output.Dumper;

import java.util.Map;

public class StackValue extends AbstractExpression {
    private StackSSALabel stackValue;

    public StackValue(StackSSALabel stackValue) {
        super(stackValue.getInferredJavaType());
        this.stackValue = stackValue;
    }

    @Override
    public Precedence getPrecedence() {
        return Precedence.WEAKEST;
    }

    @Override
    public Dumper dumpInner(Dumper d) {
        return stackValue.dump(d);
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public void collectTypeUsages(TypeUsageCollector collector) {
        stackValue.collectTypeUsages(collector);
    }

    /*
     * Makes no sense to modify so deep clone is this.
     */
    @Override
    public Expression deepClone(CloneHelper cloneHelper) {
        return this;
    }

    @Override
    public Expression replaceSingleUsageLValues(LValueRewriter lValueRewriter, SSAIdentifiers ssaIdentifiers, StatementContainer statementContainer) {
        Expression replaceMeWith = lValueRewriter.getLValueReplacement(stackValue, ssaIdentifiers, statementContainer);
        if (replaceMeWith != null) {
            return replaceMeWith;
        }
        return this;
    }

    @Override
    public Expression applyExpressionRewriter(ExpressionRewriter expressionRewriter, SSAIdentifiers ssaIdentifiers, StatementContainer statementContainer, ExpressionRewriterFlags flags) {
        stackValue = expressionRewriter.rewriteExpression(stackValue, ssaIdentifiers, statementContainer, flags);
        return this;
    }

    @Override
    public Expression applyReverseExpressionRewriter(ExpressionRewriter expressionRewriter, SSAIdentifiers ssaIdentifiers, StatementContainer statementContainer, ExpressionRewriterFlags flags) {
        return applyExpressionRewriter(expressionRewriter, ssaIdentifiers, statementContainer, flags);
    }

    public StackSSALabel getStackValue() {
        return stackValue;
    }

    @Override
    public void collectUsedLValues(LValueUsageCollector lValueUsageCollector) {
        lValueUsageCollector.collect(stackValue);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof StackValue)) return false;
        StackValue other = (StackValue) o;
        return stackValue.equals(other.stackValue);
    }

    @Override
    public int hashCode() {
        return stackValue.hashCode();
    }

    @Override
    public boolean equivalentUnder(Object o, EquivalenceConstraint constraint) {
        if (o == this) return true;
        if (!(o instanceof StackValue)) return false;
        StackValue other = (StackValue) o;
        return constraint.equivalent(stackValue, other.stackValue);
    }

    @Override
    public Literal getComputedLiteral(Map<LValue, Literal> display) {
        return display.get(stackValue);
    }
}
