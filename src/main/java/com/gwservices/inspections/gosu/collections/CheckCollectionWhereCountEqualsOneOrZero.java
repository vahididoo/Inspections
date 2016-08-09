package com.gwservices.inspections.gosu.collections;

import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import gw.gosu.ij.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Created by vmansoori on 7/31/2016.
 */
public class CheckCollectionWhereCountEqualsOneOrZero extends BaseCollectionChecker {

    private static final List<String> interestedKeywords = Arrays.asList("where", "wheretypeis");

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        super.buildVisitor(holder, isOnTheFly);
        return new GosuElementVisitor() {
            @Override
            public void visitBinaryExpression(@NotNull PsiBinaryExpression expression) {
                checkComparisonWithOneOrZero(expression, holder);
            }

        };
    }

    @Override
    protected boolean isCallerOfInterestedName(String callerName) {
        return interestedKeywords.contains(callerName != null ? callerName.toLowerCase() : "");
    }

    @Override
    protected boolean isCalledOfInterestedType(PsiType returnType) {
        return true;
    }

    @Override
    protected boolean isCalledOfInterestedName(String calledName) {
        return isMatch(calledName, "count");
    }

    @Override
    protected void registerProblem(PsiElement expression, ProblemsHolder holder) {
        holder.registerProblem(expression, "Use of where with limiting subsequent functions.", ProblemHighlightType
                .GENERIC_ERROR_OR_WARNING);
    }

}
