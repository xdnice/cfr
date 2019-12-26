package org.benf.cfr.reader.bytecode.analysis.parse.expression;

import org.benf.cfr.reader.bytecode.analysis.parse.Expression;
import org.benf.cfr.reader.bytecode.analysis.parse.LValue;
import org.benf.cfr.reader.bytecode.analysis.parse.StatementContainer;
import org.benf.cfr.reader.bytecode.analysis.parse.expression.misc.Precedence;
import org.benf.cfr.reader.bytecode.analysis.parse.rewriters.CloneHelper;
import org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriter;
import org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriterFlags;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.EquivalenceConstraint;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.LValueRewriter;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.LValueUsageCollector;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.SSAIdentifiers;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.scope.LValueScopeDiscoverer;
import org.benf.cfr.reader.bytecode.analysis.types.JavaIntersectionTypeInstance;
import org.benf.cfr.reader.bytecode.analysis.types.discovery.InferredJavaType;
import org.benf.cfr.reader.state.TypeUsageCollector;
import org.benf.cfr.reader.util.StringUtils;
import org.benf.cfr.reader.util.output.Dumper;

import java.util.List;

public class LambdaExpression extends AbstractExpression implements LambdaExpressionCommon {

    private List<LValue> args;
    private Expression result;

    public LambdaExpression(InferredJavaType castJavaType, List<LValue> args, Expression result) {
        super(castJavaType);
        this.args = args;
        this.result = result;
    }

    @Override
    public Expression deepClone(CloneHelper cloneHelper) {
        return new LambdaExpression(getInferredJavaType(), cloneHelper.replaceOrClone(args), cloneHelper.replaceOrClone(result));
    }

    @Override
    public void collectTypeUsages(TypeUsageCollector collector) {
        collector.collectFrom(args);
        result.collectTypeUsages(collector);
    }

    @Override
    public Expression replaceSingleUsageLValues(LValueRewriter lValueRewriter, SSAIdentifiers ssaIdentifiers, StatementContainer statementContainer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression applyExpressionRewriter(ExpressionRewriter expressionRewriter, SSAIdentifiers ssaIdentifiers, StatementContainer statementContainer, ExpressionRewriterFlags flags) {
        for (int x = 0; x < args.size(); ++x) {
            args.set(x, expressionRewriter.rewriteExpression(args.get(x), ssaIdentifiers, statementContainer, flags));
        }
        result = expressionRewriter.rewriteExpression(result, ssaIdentifiers, statementContainer, flags);
        return this;
    }

    @Override
    public Expression applyReverseExpressionRewriter(ExpressionRewriter expressionRewriter, SSAIdentifiers ssaIdentifiers, StatementContainer statementContainer, ExpressionRewriterFlags flags) {
        result = expressionRewriter.rewriteExpression(result, ssaIdentifiers, statementContainer, flags);
        for (int x = args.size()-1; x >= 0; --x) {
            args.set(x, expressionRewriter.rewriteExpression(args.get(x), ssaIdentifiers, statementContainer, flags));
        }
        return this;
    }

    @Override
    public Precedence getPrecedence() {
        return Precedence.PAREN_SUB_MEMBER;
    }

    @Override
    public Dumper dumpInner(Dumper d) {
        boolean multi = args.size() != 1;
        boolean first = true;
        if (getInferredJavaType().getJavaTypeInstance() instanceof JavaIntersectionTypeInstance) {
            d.separator("(").dump(getInferredJavaType().getJavaTypeInstance()).separator(")");
        }
        if (multi) d.separator("(");
        for (LValue lValue : args) {
            first = StringUtils.comma(first, d);
            d.dump(lValue);
        }
        if (multi) d.separator(")");
        d.print(" -> ").dump(result);
        d.removePendingCarriageReturn();
        return d;
    }

    /*
     * The RHS of a lambda expression has already been fully processed and and does not require further gathering
     * EXCEPT.... we need to know about local class sentinels.
     *
     * So adapt the collector.
     */
    @Override
    public void collectUsedLValues(LValueUsageCollector lValueUsageCollector) {
        // Fugly.  TODO: Fix interface.
        if (lValueUsageCollector instanceof LValueScopeDiscoverer) {
            if (((LValueScopeDiscoverer) lValueUsageCollector).descendLambdas()) {
                LValueScopeDiscoverer discover = (LValueScopeDiscoverer) lValueUsageCollector;
                result.collectUsedLValues(discover);
            }
        }
    }

    public List<LValue> getArgs() {
        return args;
    }

    public Expression getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LambdaExpression that = (LambdaExpression) o;

        if (args != null ? !args.equals(that.args) : that.args != null) return false;
        if (result != null ? !result.equals(that.result) : that.result != null) return false;

        return true;
    }

    @Override
    public final boolean equivalentUnder(Object o, EquivalenceConstraint constraint) {
        if (o == null) return false;
        if (o == this) return true;
        if (getClass() != o.getClass()) return false;
        LambdaExpression other = (LambdaExpression) o;
        if (!constraint.equivalent(args, other.args)) return false;
        if (!constraint.equivalent(result, other.result)) return false;
        return true;
    }

}
