package com.gwservices.inspections.gosu.collections;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiElementVisitor;
import gw.gosu.ij.psi.GosuElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/31/2016.
 */
public class CheckCollectionWhereWhereTypeIs extends BaseCollectionChecker {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        super.buildVisitor(holder, isOnTheFly);
        return new GosuElementVisitor() {
            @Override
            public void visitBinaryExpression(@NotNull PsiBinaryExpression expression) {
                super.visitBinaryExpression(expression);
                checkComparisonWith1or0(expression, holder);
            }
        };
    }

    @Override
    protected String getSuggestion() {
        return "hasMatch";
    }

    @Override
    protected String getCalled() {
        return "count";
    }

    @Override
    protected String getCaller() {
        return "whereTypeIs";
    }
}
