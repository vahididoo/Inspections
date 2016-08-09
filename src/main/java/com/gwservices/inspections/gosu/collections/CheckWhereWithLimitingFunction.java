package com.gwservices.inspections.gosu.collections;

import com.intellij.codeInspection.*;
import com.intellij.psi.*;

import java.util.*;

/**
 * Created by vmansoori on 8/2/2016.
 */
public class CheckWhereWithLimitingFunction extends BaseCollectionChecker {
    private static final List<String> interestedCalledKeywords = Arrays.asList("haselements", "wheretypeis",
            "contains", "firstwhere", "first", "single", "singlewhere", "where");
    private static final List<String> interestedCallerKeywords = Arrays.asList("wheretypeis", "where");

    @Override
    protected boolean isCalledOfInterestedType(PsiType returnType) {
        return true;
    }

    @Override
    protected boolean isCalledOfInterestedName(String calledName) {
        return interestedCalledKeywords.contains(calledName != null ? calledName.toLowerCase() : "");
    }

    @Override
    protected boolean isCallerOfInterestedName(String callerName) {

        return interestedCallerKeywords.contains(callerName != null ? callerName.toLowerCase() : "");
    }

    @Override
    protected void registerProblem(PsiElement expression, ProblemsHolder holder) {
        holder.registerProblem(expression, "Use of where with limiting subsequent functions.", ProblemHighlightType
                .GENERIC_ERROR_OR_WARNING);
    }
}