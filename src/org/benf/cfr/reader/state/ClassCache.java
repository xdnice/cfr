package org.benf.cfr.reader.state;

import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair;
import org.benf.cfr.reader.bytecode.analysis.types.ClassNameUtils;
import org.benf.cfr.reader.bytecode.analysis.types.JavaRefTypeInstance;
import org.benf.cfr.reader.bytecode.analysis.types.TypeConstants;
import org.benf.cfr.reader.util.collections.MapFactory;
import org.benf.cfr.reader.util.collections.SetFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ClassCache {

    private final Map<String, JavaRefTypeInstance> refClassTypeCache = MapFactory.newMap();
    // We want to avoid generating names which collide with classes.
    // This is a nice simple check.
    private final Set<String> simpleClassNamesSeen = SetFactory.newSet();
    private final Map<String, String> renamedClasses = MapFactory.newMap();

    private final DCCommonState dcCommonState;

    ClassCache(DCCommonState dcCommonState) {
        this.dcCommonState = dcCommonState;
        // TODO:  Not sure I need to do this any more.
        add(TypeConstants.ASSERTION_ERROR.getRawName(), TypeConstants.ASSERTION_ERROR);
        add(TypeConstants.OBJECT.getRawName(), TypeConstants.OBJECT);
        add(TypeConstants.STRING.getRawName(), TypeConstants.STRING);
        add(TypeConstants.ENUM.getRawName(), TypeConstants.ENUM);
    }

    public JavaRefTypeInstance getRefClassFor(String rawClassName) {
        /*
         * If the path (or pseudopath) has been renamed because it's a collision,
         * we need to replace with the deduplicated version - otherwise the file
         * will not match the type.
         */
        String originalRawClassName = ClassNameUtils.convertToPath(rawClassName);
        rawClassName = dcCommonState.getPossiblyRenamedFileFromClassFileSource(originalRawClassName);
        String name = ClassNameUtils.convertFromPath(rawClassName);
        JavaRefTypeInstance typeInstance = refClassTypeCache.get(name);
        String originalName = null;
        if (!rawClassName.equals(originalRawClassName)) {
            originalName = ClassNameUtils.convertFromPath(originalRawClassName);
        }
        if (typeInstance == null) {
            typeInstance = JavaRefTypeInstance.create(name, dcCommonState);
            add(name, originalName, typeInstance);
        }
        return typeInstance;
    }

    private void add(String name, JavaRefTypeInstance typeInstance) {
        add(name, null, typeInstance);
    }

    private void add(String name, String originalName, JavaRefTypeInstance typeInstance) {
        refClassTypeCache.put(name, typeInstance);
        simpleClassNamesSeen.add(typeInstance.getRawShortName());
        if (originalName != null) {
            renamedClasses.put(name, originalName);
        }
    }

    public boolean isClassName(String name) {
        return simpleClassNamesSeen.contains(name);
    }

    public Pair<JavaRefTypeInstance, JavaRefTypeInstance> getRefClassForInnerOuterPair(String rawInnerName, String rawOuterName) {
        String innerName = ClassNameUtils.convertFromPath(rawInnerName);
        String outerName = ClassNameUtils.convertFromPath(rawOuterName);
        JavaRefTypeInstance inner = refClassTypeCache.get(innerName);
        JavaRefTypeInstance outer = refClassTypeCache.get(outerName);
        if (inner != null && outer != null) return Pair.make(inner, outer);
        Pair<JavaRefTypeInstance, JavaRefTypeInstance> pair = JavaRefTypeInstance.createKnownInnerOuter(innerName, outerName, outer, dcCommonState);
        if (inner == null) {
            add(innerName, pair.getFirst());
            inner = pair.getFirst();
        }
        if (outer == null) {
            add(outerName, pair.getSecond());
            outer = pair.getSecond();
        }
        return Pair.make(inner, outer);

    }

    public Collection<JavaRefTypeInstance> getLoadedTypes() {
        return refClassTypeCache.values();
    }

    String getOriginalName(String typeName) {
        return renamedClasses.get(typeName);
    }
}
