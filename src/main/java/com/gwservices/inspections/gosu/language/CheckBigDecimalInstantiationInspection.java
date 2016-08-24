package com.gwservices.inspections.gosu.language;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import gw.gosu.ij.psi.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 8/19/2016.
 * todo provide a quick fix
 */
public class CheckBigDecimalInstantiationInspection extends BaseLocalInspectionTool {

    private static class BigDecimalQuickFix extends LocalQuickFixOnPsiElement {

        protected BigDecimalQuickFix(@NotNull PsiElement element) {
            super(element);
        }

        @NotNull
        @Override
        public String getText() {
            return "Replace with numeric literal";
        }

        @Override
        public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement,
                           @NotNull PsiElement endElement) {
            if (((PsiNewExpression) startElement).getArgumentList().getExpressions().length == 1) {
                PsiExpression psiExpression = ((PsiNewExpression) startElement).getArgumentList().getExpressions()[0];
                if (psiExpression instanceof GosuPsiLiteralExpression) {
                    String text = String.valueOf(((GosuPsiLiteralExpression) psiExpression).getValue());
                    if (text != null) {
                        PsiExpression newElement = GosuPsiElementFactory.SERVICE.getInstance(project)
                                                                                .createExpressionFromText(text +
                                                                                        getTypeStr(startElement),
                                                                                        startElement.getContext());
                        startElement.replace(newElement);
                    }

                }
            }

        }

        private String getTypeStr(PsiElement startElement) {
            if (startElement instanceof PsiNewExpression) {
                PsiType type = ((PsiNewExpression) startElement).getType();
                if (InheritanceUtil.isInheritor(type, "java.math.BigDecimal")) {
                    return "bd";
                }
                if (InheritanceUtil.isInheritor(type, "java.math.BigInteger")) {
                    return "bi";
                }
            }
            return "";
        }

        @Nls
        @NotNull
        @Override
        public String getFamilyName() {
            return "Replace with <number>bd or <number>bi";
        }

    }

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
                                    .WEAK_WARNING, new BigDecimalQuickFix(expression));
                        }
                    }
                }
            }
        };
    }
}
