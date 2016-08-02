package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import gw.gosu.ij.psi.GosuElementVisitor;
import gw.gosu.ij.psi.GosuPsiLiteralExpression;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/18/2016.
 */
public class CheckUseOfNewDate extends BaseLocalInspectionTool {

    public static final String TEMPLATE = "Use of <code>new Date()</code> to get the date is discouraged.<p>date is not affected by testing class</p>";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {

        return new GosuElementVisitor() {


            @Override
            public void visitNewExpression(PsiNewExpression newExpression) {
                //todo fix NPE
                PsiClass psiClass = PsiTypesUtil.getPsiClass(newExpression.getType());
                if (psiClass != null && "java.util.date".equalsIgnoreCase(psiClass.getQualifiedName())) {
                    for (PsiElement psiElement : newExpression.getChildren()) {
                        if (psiElement instanceof PsiExpressionList) {
                            if (0 == psiElement.getChildren().length) {
                                holder.registerProblem(newExpression, TEMPLATE, ProblemHighlightType.WEAK_WARNING);
                            } else {
                                for (PsiElement element : psiElement.getChildren()) {
                                    if (element instanceof GosuPsiLiteralExpression) {
                                        return;
                                    }
                                }
                                holder.registerProblem(newExpression, TEMPLATE, ProblemHighlightType.WEAK_WARNING);
                            }
                        }
                    }
                }
            }
        };
    }
}
