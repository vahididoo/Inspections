package com.gwservices.inspections.gosu.language;

import com.gwservices.inspections.gosu.*;
import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;

/**
 * Created by vmansoori on 8/19/2016.
 */
public class CheckBigDecimalValuOfInspection extends BaseCallerCalleeChecker {
    @Override
    protected boolean isCallerOfInterestedType(PsiType returnType) {
        return InheritanceUtil.isInheritor(returnType, "java.math.BigDecimal") || InheritanceUtil.isInheritor
                (returnType, "java.math.BigInteger");
    }

    @Override
    protected boolean isCalledOfInterestedType(PsiType returnType) {
        return InheritanceUtil.isInheritor(returnType, "java.math.BigDecimal") || InheritanceUtil.isInheritor
                (returnType, "java.math.BigInteger");
    }

    @Override
    protected boolean isCalledOfInterestedName(String calledName) {
        return "valueof".equalsIgnoreCase(calledName);
    }

    @Override
    protected boolean isCallerOfInterestedName(String callerName) {
        return true;
    }

    @Override
    protected void registerProblem(PsiElement expression, ProblemsHolder holder) {

    }
}
