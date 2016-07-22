package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.expressions.GosuMethodCallExpressionImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/22/2016.
 */
public class ActivityPatternComparison extends BaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitMethodCallExpression(GosuMethodCallExpressionImpl methodCallExpression) {
                super.visitMethodCallExpression(methodCallExpression);
            }
        }
    }
}
