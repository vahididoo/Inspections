package com.gwservices.inspections.gosu.language;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import gw.gosu.ij.psi.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 8/19/2016.
 */
public class CheckNewEntityBundleImplInspection extends BaseLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitNewExpression(PsiNewExpression expression) {
                if (expression != null) {
                    PsiMethod constructor = expression.resolveMethod();
                    if (constructor != null) {
                        PsiClass psiClass = constructor.getContainingClass();
                        if (InheritanceUtil.isInheritor(psiClass, "com.guidewire.pl.system.bundle.EntityBundleImpl")) {
                            holder.registerProblem(expression, "Use new entityBundleImpl", ProblemHighlightType
                                    .WEAK_WARNING);
                        }
                    }
                }
            }
        };
    }

}
