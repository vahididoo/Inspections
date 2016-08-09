package com.gwservices.inspections.gosu.collections;

import com.gwservices.inspections.gosu.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;

/**
 * Created by vmansoori on 7/31/2016.
 */
public abstract class BaseCollectionChecker extends BaseCallerCalleeInspection {

    @Override
    protected boolean isCallerOfInterestedType(PsiType returnType) {
        return InheritanceUtil.isInheritor(returnType, "java.util.Collection");
    }

}
