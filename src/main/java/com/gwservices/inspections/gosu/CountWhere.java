package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.psi.api.expressions.IGosuExpression;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.expressions.GosuBeanMethodCallExpressionImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuFieldAccessExpressionImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/17/2016.
 */
public class CountWhere extends BaseLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitBeanMethodCallExpression(GosuBeanMethodCallExpressionImpl callExpression) {
                super.visitBeanMethodCallExpression(callExpression);
                if ("countwhere".equalsIgnoreCase(callExpression.getReferenceName())) {
                    holder.registerProblem(callExpression, "Consider using query builder instead of countWhere");
                }
            }
        };
    }
}
