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
public class CheckBigDecimalInstantiationInspection extends BaseLocalInspectionTool {

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
                        if (InheritanceUtil.isInheritor(psiClass, "java.math.BigDecimal") || InheritanceUtil
                                .isInheritor(psiClass, "java.math.BigInteger")) {
                            holder.registerProblem(expression, "Use <number>bd instead.", ProblemHighlightType
                                    .WEAK_WARNING);
                        }
                    }
                }
            }
        };
    }

}
