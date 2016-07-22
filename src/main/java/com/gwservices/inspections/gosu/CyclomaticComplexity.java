package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.expressions.IGosuExpression;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/19/2016.
 */
public class CyclomaticComplexity extends BaseLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {

        return new GosuElementVisitor() {
            @Override
            public void visitExpression(IGosuExpression expression) {
                System.out.println(expression.getType());
            }

            @Override
            public void visitElement(IGosuPsiElement element) {
                System.out.println(element.getClass());
            }
        };
    }


}
