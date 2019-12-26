package org.benf.cfr.reader.entities.innerclass;

import org.benf.cfr.reader.bytecode.analysis.types.JavaTypeInstance;
import org.benf.cfr.reader.entities.AccessFlag;
import org.benf.cfr.reader.util.annotation.Nullable;

import java.util.Set;

public class InnerClassAttributeInfo {
    private final
    @Nullable
    JavaTypeInstance innerClassInfo;
    private final
    @Nullable
    JavaTypeInstance outerClassInfo;
    private final
    @Nullable
    String innerName;
    private final Set<AccessFlag> accessFlags;

    public InnerClassAttributeInfo(JavaTypeInstance innerClassInfo, JavaTypeInstance outerClassInfo, String innerName, Set<AccessFlag> accessFlags) {
        this.innerClassInfo = innerClassInfo;
        this.outerClassInfo = outerClassInfo;
        this.innerName = innerName;
        this.accessFlags = accessFlags;
    }

    public JavaTypeInstance getInnerClassInfo() {
        return innerClassInfo;
    }

    private JavaTypeInstance getOuterClassInfo() {
        return outerClassInfo;
    }

    private String getInnerName() {
        return innerName;
    }

    public Set<AccessFlag> getAccessFlags() {
        return accessFlags;
    }
}
