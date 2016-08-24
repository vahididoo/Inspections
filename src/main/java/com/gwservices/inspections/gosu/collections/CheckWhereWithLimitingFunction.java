package com.gwservices.inspections.gosu.collections;

import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import com.siyeh.ig.ui.*;

/**
 * Created by vmansoori on 8/2/2016.
 */
public class CheckWhereWithLimitingFunction extends BaseCollectionChecker {
    private static final ExternalizableStringSet interestedCalledKeywords = new ExternalizableStringSet
            ("haselements", "wheretypeis", "contains", "firstwhere", "first", "single", "singlewhere", "where");
    private static final ExternalizableStringSet interestedCallerKeywords = new ExternalizableStringSet
            ("wheretypeis", "where");

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
        holder.registerProblem(expression, "Use of <code>#ref</code>", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
    }

    @Override
    public ExternalizableStringSet getCallerList() {
        return interestedCallerKeywords;
    }

    @Override
    public ExternalizableStringSet getCalledList() {
        return interestedCalledKeywords;
    }
}
