package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import gw.gosu.ij.psi.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 8/28/2016.
 */
public class CheckExplicitCommitRollback extends BaseLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitLambdaExpression(PsiLambdaExpression expression) {
                if (expression.getParent() instanceof PsiReferenceExpression) {
                    PsiElement resolve = ((PsiReferenceExpression) expression.getParent()).resolve();
                    if (resolve instanceof PsiMethod && InheritanceUtil.isInheritor(((PsiMethod) resolve)
                            .getReturnType(), "gw.api.Transaction")) {

                        expression.

                    }
                }
            }
        };
    }
}
