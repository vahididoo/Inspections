package com.gwservices.inspections.gosu.query;

import com.gwservices.inspections.gosu.*;
import com.intellij.codeInspection.*;
import com.intellij.psi.*;

import java.util.*;

/**
 * Created by vmansoori on 7/30/2016.
 */
public class CheckSelectWithLimitingFunctions extends BaseSelectChecker {

    private static final List<String> interestedKeywords = Arrays.asList("first", "firstresult", "getfirstresult",
            "firstwhere", "getatmostonerow", "single", "singlewhere", "where", "wheretypeis");

    @Override
    protected boolean isCalledOfInterestedType(PsiType returnType) {
        return true;
    }

    @Override
    protected boolean isCalledOfInterestedName(String callerName) {
        return interestedKeywords.contains(callerName != null ? callerName.toLowerCase() : "");
    }

    @Override
    protected void registerProblem(PsiElement expression, ProblemsHolder holder) {
        holder.registerProblem(expression, "Use of select(...) with limiting functions.",
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
    }

}
