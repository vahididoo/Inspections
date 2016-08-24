package com.gwservices.inspections.gosu.query;

import com.gwservices.inspections.gosu.*;
import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import com.siyeh.ig.ui.*;

/**
 * Created by vmansoori on 7/30/2016.
 */
public class CheckSelectWithLimitingFunctions extends BaseSelectChecker {

    private static final ExternalizableStringSet interestedKeywords = new ExternalizableStringSet("firstwhere",
            "singlewhere", "where", "wheretypeis");

    @Override
    public ExternalizableStringSet getCallerList() {
        return null;
    }

    @Override
    public ExternalizableStringSet getCalledList() {
        return interestedKeywords;
    }

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
        holder.registerProblem(expression, "Use of select(...) with limiting functions.", ProblemHighlightType
                .GENERIC_ERROR_OR_WARNING);
    }

}
