package com.gwservices.inspections.gosu.collections;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReferenceExpression;
import gw.gosu.ij.psi.GosuElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/31/2016.
 */
public class CheckCollectionWhereTypeIsHasElements extends BaseCollectionChecker {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                super.visitReferenceExpression(expression);
                checkReferenceExpression(expression,expression, holder);
            }
        };

    }



    @Override
    protected String getCalled() {
        return "hasElements";
    }

    @Override
    protected String getCaller() {
        return "Where";
    }

    @Override
    protected String getSuggestion() {
        return "hasMatch";
    }
}
