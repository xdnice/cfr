package org.benf.cfr.reader.bytecode.analysis.types;

import org.benf.cfr.reader.state.TypeUsageCollector;
import org.benf.cfr.reader.util.TypeUsageCollectable;
import org.benf.cfr.reader.util.collections.ListFactory;
import org.benf.cfr.reader.util.collections.MapFactory;
import org.benf.cfr.reader.util.output.Dumpable;
import org.benf.cfr.reader.util.output.Dumper;

import java.util.List;
import java.util.Map;

public class FormalTypeParameter implements Dumpable, TypeUsageCollectable {
    private String name;
    private JavaTypeInstance classBound;
    private JavaTypeInstance interfaceBound;

    public FormalTypeParameter(String name, JavaTypeInstance classBound, JavaTypeInstance interfaceBound) {
        this.name = name;
        this.classBound = classBound;
        this.interfaceBound = interfaceBound;
    }

    public static Map<String, FormalTypeParameter> getMap(List<FormalTypeParameter> formalTypeParameters) {
        Map<String, FormalTypeParameter> res = MapFactory.newMap();
        if (formalTypeParameters != null) {
            for (FormalTypeParameter p : formalTypeParameters) {
                res.put(p.getName(), p);
            }
        }
        return res;
    }

    public String getName() {
        return name;
    }

    @Override
    public void collectTypeUsages(TypeUsageCollector collector) {
        collector.collect(classBound);
        collector.collect(interfaceBound);
    }

    public void add(FormalTypeParameter other) {
        JavaTypeInstance typ = getBound();
        JavaTypeInstance otherTyp = other.getBound();
        if (typ instanceof JavaIntersectionTypeInstance) {
            typ = ((JavaIntersectionTypeInstance) typ).withPart(otherTyp);
        } else {
            typ = new JavaIntersectionTypeInstance(ListFactory.newList(typ, otherTyp));
        }
        if (classBound != null) {
            classBound = typ;
        } else {
            interfaceBound = typ;
        }
    }

    public JavaTypeInstance getBound() {
        return classBound == null ? interfaceBound : classBound;
    }

    @Override
    public Dumper dump(Dumper d) {
        JavaTypeInstance dispInterface = getBound();
        d.print(name);
        if (dispInterface != null) {
            if (!"java.lang.Object".equals(dispInterface.getRawName())) {
                d.print(" extends ").dump(dispInterface);
            }
        }
        return d;
    }

    @Override
    public String toString() {
        return name + " [ " + classBound + "|" + interfaceBound + "]";
    }
}
