package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.expressions.GosuNewExpressionImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/18/2016.
 */
public class NewDate extends BaseLocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitNewExpression(GosuNewExpressionImpl newExpression) {
                if(newExpression.getType().equalsToText("java.util.Date")){
                    holder.registerProblem(newExpression,"Use of new Date() to get the date is discouraged.");
                }
            }
        };
    }
}
